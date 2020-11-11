package com.molicloud.mqr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 机器人群成员
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="RobotGroupMember对象", description="机器人群成员")
public class RobotGroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "群ID")
    private String gid;

    @ApiModelProperty(value = "成员ID")
    private String mid;

    @ApiModelProperty(value = "金币")
    private BigDecimal gold;

    @ApiModelProperty(value = "持有的插件钩子")
    private String holdPluginHook;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "删除标记（0：正常，1：已删除）")
    private Boolean deleted;

}
