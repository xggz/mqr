package com.molicloud.mqr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 机器人插件
 * </p>
 *
 * @author feitao
 * @since 2020-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="RobotPlugin对象", description="机器人插件")
public class RobotPlugin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "插件名")
    private String name;

    @ApiModelProperty(value = "插件类名")
    private String className;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "主页")
    private String homeUrl;

    @ApiModelProperty(value = "说明")
    private String explain;

    @ApiModelProperty(value = "版本代号")
    private Integer version;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除标记（0：正常，1：已删除）")
    private Boolean deleted;
}