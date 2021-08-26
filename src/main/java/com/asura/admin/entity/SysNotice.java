package com.asura.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * <p>
 * 通知公告表
 * </p>
 *
 * @author Rhett
 * @since 2021-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="SysNotice对象", description="通知公告表")
public class SysNotice implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公告ID")
    @TableId(value = "notice_id", type = IdType.AUTO)
    private Integer noticeId;

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    @ApiModelProperty(value = "公告标题")
    private String noticeTitle;

    @ApiModelProperty(value = "公告类型（1通知 2公告）")
    private String noticeType;

    @ApiModelProperty(value = "公告内容")
    private String noticeContent;

    @ApiModelProperty(value = "公告状态（0正常 1关闭）")
    private String status;

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

    @TableField(exist = false)
    @ApiModelProperty(value = "请求参数")
    private Map<String, Object> params = new HashMap<>();
}
