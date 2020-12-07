package com.molicloud.mqr.plugin.votekick.entity;

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
 * 投票踢人
 * </p>
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/12/04 3:55 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "robot_plugin_votekick")
public class RobotPluginVoteKick implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 群ID
     */
    @TableField(value = "group_id")
    private String groupId;

    /**
     * 投票人QQ
     */
    @TableField(value = "from_qq")
    private String fromQq;

    /**
     * 被投票人QQ
     */
    @TableField(value = "qq")
    private String qq;

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
