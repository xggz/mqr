package com.molicloud.mqr.plugin.signin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 签到插件记录
 * </p>
 *
 * @author wispx wisp-x@qq.com
 * @since 2020-12-03 2:48 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "robot_plugin_signin")
public class RobotPluginSignIn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群ID
     */
    @TableField(value = "group_id")
    private String groupId;

    /**
     * 签到人QQ
     */
    @TableField(value = "qq")
    private String qq;

    /**
     * 是否连续签到
     */
    @TableField(value = "is_continuity")
    private Boolean isContinuity;

    /**
     * 格言
     */
    @TableField(value = "motto")
    private String motto;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
