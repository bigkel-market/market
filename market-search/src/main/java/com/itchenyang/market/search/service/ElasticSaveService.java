package com.itchenyang.market.search.service;

import com.itchenyang.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author BigKel
 * @createTime 2022/9/17
 */
public interface ElasticSaveService {

    public boolean saveProduct(List<SkuEsModel> models) throws IOException;
}
