package com.itchenyang.market.member.dao;

import com.itchenyang.market.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 16:46:11
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
