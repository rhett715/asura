package com.asura.admin.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "BaseEntity", description = "Entity基类")
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "搜索值")
    private String searchValue;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();
}
