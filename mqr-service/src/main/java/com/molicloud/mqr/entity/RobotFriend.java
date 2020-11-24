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
 * 机器人好友
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="RobotFriend对象", description="机器人好友")
public class RobotFriend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "好友ID")
    private String fid;

    @ApiModelProperty(value = "持有的插件钩子")
    private String holdPluginHook;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除标记（0：正常，1：已删除）")
    private Boolean deleted;

}
