package com.itchenyang.market.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@Data
@TableName("flyway_schema_history")
public class FlywaySchemaHistoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer installedRank;
	/**
	 * 
	 */
	private String version;
	/**
	 * 
	 */
	private String description;
	/**
	 * 
	 */
	private String type;
	/**
	 * 
	 */
	private String script;
	/**
	 * 
	 */
	private Integer checksum;
	/**
	 * 
	 */
	private String installedBy;
	/**
	 * 
	 */
	private Date installedOn;
	/**
	 * 
	 */
	private Integer executionTime;
	/**
	 * 
	 */
	private Integer success;

}
