package com.itchenyang.market.search.service;

import com.itchenyang.market.search.vo.SearchParam;
import com.itchenyang.market.search.vo.SearchResult;

/**
 * @author BigKel
 * @createTime 2022/10/15
 */
public interface MallSearchService {
    SearchResult search(SearchParam param);
}
