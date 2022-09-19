package com.itchenyang.market.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.itchenyang.common.to.es.SkuEsModel;
import com.itchenyang.market.search.config.ElasticConfig;
import com.itchenyang.market.search.constant.EsConstant;
import com.itchenyang.market.search.service.ElasticSaveService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/9/17
 */
@Service
public class ElasticSaveServiceImpl implements ElasticSaveService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Override
    public boolean saveProduct(List<SkuEsModel> models) throws IOException {
        // 1、给es中建立索引以及映射关系

        // 2、批量保存数据
        BulkRequest bulkRequest = new BulkRequest();
        models.forEach(model -> {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());
            String saveInfo = JSON.toJSONString(model);
            indexRequest.source(saveInfo, XContentType.JSON);

            bulkRequest.add(indexRequest);
        });

        BulkResponse response = restHighLevelClient.bulk(bulkRequest, ElasticConfig.COMMON_OPTIONS);
        return !response.hasFailures();
    }
}
