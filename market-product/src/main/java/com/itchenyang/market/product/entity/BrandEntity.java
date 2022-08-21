package com.itchenyang.market.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.itchenyang.common.valid.AddGroupVaild;
import com.itchenyang.common.valid.StatusValid;
import com.itchenyang.common.valid.UpdateGroupVaild;
import com.itchenyang.common.valid.UpdateStatusGroupVaild;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author bigkel
 * @email 1151094976@qq.com
 * @date 2022-07-30 11:50:33
 */
@Data
@TableName("pms_brand")
@AllArgsConstructor
@NoArgsConstructor
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@Null(message = "新增不能有brandId", groups = {AddGroupVaild.class})
	@NotNull(message = "修改必须有brandId", groups = {UpdateGroupVaild.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups = {AddGroupVaild.class, UpdateGroupVaild.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotNull(message = "logo不能为空", groups = {AddGroupVaild.class})
	@URL(message = "URL必须合法", groups = {AddGroupVaild.class, UpdateGroupVaild.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@StatusValid(values = {0, 1}, groups = {AddGroupVaild.class, UpdateStatusGroupVaild.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(message = "检索首字母不能为空", groups = {AddGroupVaild.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "首字母应在a-z或A-Z之间", groups = {AddGroupVaild.class, UpdateGroupVaild.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序字段不能为空", groups = {AddGroupVaild.class})
	@Min(value = 0, message = "排序必须大于等于0", groups = {AddGroupVaild.class, UpdateGroupVaild.class})
	private Integer sort;

}
