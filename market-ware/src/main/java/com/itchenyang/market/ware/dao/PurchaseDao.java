package com.itchenyang.market.ware.dao;

import com.itchenyang.market.ware.entity.PurchaseDetailEntity;
import com.itchenyang.market.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购信息
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:48:59
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {

    void updateStatusByPurchaseId(@Param("purchaseDetailEntities") List<PurchaseDetailEntity> purchaseDetailEntities);
}
