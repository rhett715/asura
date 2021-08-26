package com.asura.admin.common.core.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Rhett
 * @Date 2021/8/18
 * @Description
 */
@Data
@NoArgsConstructor
@ApiModel(value = "TableDataInfo<T>", description = "表格分页数据对象")
public class TableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("总记录数")
    private long total;

    @ApiModelProperty("列表数据")
    private List<T> rows;

    @ApiModelProperty("消息状态码")
    private int code;

    @ApiModelProperty("消息内容")
    private String msg;

    /**
     * 分页
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, long total) {
        this.rows = list;
        this.total = total;
    }
}
