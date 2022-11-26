package com.itchenyang.market.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.itchenyang.common.to.es.SkuEsModel;
import com.itchenyang.common.utils.R;
import com.itchenyang.market.search.config.ElasticConfig;
import com.itchenyang.market.search.constant.EsConstant;
import com.itchenyang.market.search.feign.ProductFeignService;
import com.itchenyang.market.search.service.MallSearchService;
import com.itchenyang.market.search.vo.AttrResponseVo;
import com.itchenyang.market.search.vo.BrandVo;
import com.itchenyang.market.search.vo.SearchParam;
import com.itchenyang.market.search.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BigKel
 * @createTime 2022/10/15
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient client;

    @Resource
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult result = new SearchResult();

        // 1、组装DSL语句
        SearchRequest request = builderRequest(param);
        try {
            // 2、执行ES查询请求
            SearchResponse response = client.search(request, ElasticConfig.COMMON_OPTIONS);

            // 3、封装查询结果
            result = builderResult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 组装request语句
     * @return
     */
    private SearchRequest builderRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        /**
         * 1、查询：keyword，过滤（属性，分类，品牌，价格区间，库存）
         */
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // keyword模糊匹配
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 分类
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 品牌
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 库存
        boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        // 价格区间 1_500   1_   _500
        if (StringUtils.isNotBlank(param.getSkuPrice())) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if (s.length == 2) {
                rangeQuery.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(s[0]);
                }
                if (param.getSkuPrice().endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        // 属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            // attrs=1_5寸:5寸 & 2_16G:8G
            for (String attr : param.getAttrs()) {
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
                nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                boolQuery.filter(QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None));
            }
        }
        sourceBuilder.query(boolQuery);

        /**
         * 排序，分页，高亮
         */
        // 排序
        if (StringUtils.isNotBlank(param.getSort())) {
            // sort=hotScore_asc/desc
            String[] s = param.getSort().split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], order);
        }
        // 分页
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        // 高亮
        if (StringUtils.isNotEmpty(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style = 'color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        // 品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1))
                .subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);
        // 分类聚合
        TermsAggregationBuilder catelog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId")
                .subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catelog_agg);
        // attr聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrs_id_agg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1))
                        .subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue")));
        sourceBuilder.aggregation(attr_agg);

        System.out.println("构建的DSL语句: " + sourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
    }

    /**
     * 封装response结果
     * @param response
     * @return
     */
    private SearchResult builderResult(SearchResponse response, SearchParam param) {
        SearchResult result = new SearchResult();
        SearchHits hits = response.getHits();
        // 查询到的所有商品信息
        List<SkuEsModel> products = new ArrayList<>();
        if (hits != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                SkuEsModel esModel = JSON.parseObject(hit.getSourceAsString(), SkuEsModel.class);
                if (param.getKeyword() != null && hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0) {
                    HighlightField highlight = hit.getHighlightFields().get("skuTitle");
                    String string = highlight.getFragments()[0].string();
                    esModel.setSkuTitle(string);
                }
                products.add(esModel);
            }
        }
        result.setProduct(products);

        // 当前页码
        result.setPageNum(param.getPageNum());

        // 总记录数
        result.setTotal(hits.getTotalHits().value);

        // 总页码
        int hasNext = (int) hits.getTotalHits().value % EsConstant.PRODUCT_PAGESIZE;
        int prePage = (int) hits.getTotalHits().value / EsConstant.PRODUCT_PAGESIZE;
        int totalPage = hasNext == 0 ? prePage : prePage + 1;
        result.setTotalPages(totalPage);

        Aggregations resAgg = response.getAggregations();
        // 当前查询到的结果，所有涉及到的品牌
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = resAgg.get("brand_agg");    // 品牌可能有多个，品牌名字和图片只有一个
        for (Terms.Bucket item : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(item.getKeyAsNumber().longValue());
            String brand_img = ((ParsedStringTerms) item.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brand_img);
            String brand_name = ((ParsedStringTerms) item.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brand_name);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        // 当前查询到的结果，所有涉及到的所有属性
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedLongTerms attr_id_agg = ((ParsedNested) resAgg.get("attr_agg")).getAggregations().get("attrs_id_agg");
        for (Terms.Bucket item : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            attrVo.setAttrId(item.getKeyAsNumber().longValue());
            String attr_name = ((ParsedStringTerms) item.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attr_name);
            List<String> attr_value = new ArrayList<>();
            for (Terms.Bucket it : ((ParsedStringTerms) item.getAggregations().get("attr_value_agg")).getBuckets()) {
                String keyAsString = it.getKeyAsString();
                attr_value.add(keyAsString);
            }
            attrVo.setAttrValue(attr_value);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        // 当前查询到的结果，所有涉及到的所有分类
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catelog_agg = resAgg.get("catalog_agg");
        for (Terms.Bucket item : catelog_agg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            catalogVo.setCatalogId(item.getKeyAsNumber().longValue());
            String catalog_name = ((ParsedStringTerms) item.getAggregations().get("catalog_name_agg")).getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalog_name);
            catalogVos.add(catalogVo);
        }
        result.setCatalogs(catalogVos);

        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // 构建面包屑导航功能
        // 属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
                // 分析每个attrs的查询参数值
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                // attr=2_5寸:6寸
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                try {
                    R info = productFeignService.info(Long.parseLong(s[0]));
                    result.getAttrIds().add(Long.parseLong(s[0]));
                    if (info.getCode() == 0) {
                        AttrResponseVo attrInfo = info.getData("attr", new TypeReference<AttrResponseVo>() {
                        });
                        navVo.setNavName(attrInfo.getAttrName());
                    } else {
                        navVo.setNavName(s[0]);
                    }
                }catch (Exception e) {
                    navVo.setNavName(s[0]);
                }
                // 叉掉当前导航时，去掉url中属于当前导航的uri
                // link保存的是当前导航被去掉的时候，url应该是哪个值
                // _queryString中保存的是url的参数
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.bigkel.com/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(collect);
        }
        // 品牌
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            try {
                R r = productFeignService.brandsInfo(param.getBrandId());
                if (r.getCode() == 0) {
                    List<BrandVo> brands = r.getData("brands", new TypeReference<List<BrandVo>>() {
                    });
                    StringBuilder buffer = new StringBuilder();
                    String replace = null;
                    for (BrandVo brand : brands) {
                        buffer.append(brand.getName()).append(";");
                        replace = replaceQueryString(param, brand.getBrandId() + "", "brandId");
                    }
                    navVo.setNavValue(buffer.toString());
                    navVo.setLink("http://search.bigkel.com/list.html?" + replace);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            navs.add(navVo);
            result.setNavs(navs);
        }

        return result;
    }

    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "20%");     // 浏览器对空格编码是20%
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (!param.get_queryString().contains("&")) {
            return param.get_queryString().replace( key + "=" + encode, "");
        }
        return param.get_queryString().replace("&" + key + "=" + encode, "");
    }
}
