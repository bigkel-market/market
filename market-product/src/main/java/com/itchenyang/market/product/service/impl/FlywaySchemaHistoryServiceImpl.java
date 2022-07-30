package com.itchenyang.market.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itchenyang.common.utils.PageUtils;
import com.itchenyang.common.utils.Query;


@Service("flywaySchemaHistoryService")
public class FlywaySchemaHistoryServiceImpl extends ServiceImpl<FlywaySchemaHistoryDao, FlywaySchemaHistoryEntity> implements FlywaySchemaHistoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<FlywaySchemaHistoryEntity> page = this.page(
                new Query<FlywaySchemaHistoryEntity>().getPage(params),
                new QueryWrapper<FlywaySchemaHistoryEntity>()
        );

        return new PageUtils(page);
    }

}