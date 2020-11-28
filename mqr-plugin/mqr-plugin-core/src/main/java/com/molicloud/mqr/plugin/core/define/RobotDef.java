package com.molicloud.mqr.plugin.core.define;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 机器人信息定义
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 6:53 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RobotDef {

    /**
     * 机器人QQ
     */
    private String qq;

    /**
     * 机器人昵称
     */
    private String nickname;

    /**
     * 管理员QQ列表
     */
    private String[] admins;

    /**
     * 群列表
     */
    private List<Group> groupList;

    /**
     * 好友列表
     */
    private List<Friend> friendList;

    /**
     * 群信息
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Group {

        /**
         * 群ID
         */
        private String id;

        /**
         * 群名
         */
        private String nick;

        /**
         * 构造函数
         *
         * @param id
         * @param nick
         */
        public Group(String id, String nick) {
            this.id = id;
            this.nick = nick;
        }
    }

    /**
     * 好友信息
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Friend {

        /**
         * 好友ID
         */
        private String id;

        /**
         * 好友昵称
         */
        private String nick;

        /**
         * 构造函数
         *
         * @param id
         * @param nick
         */
        public Friend(String id, String nick) {
            this.id = id;
            this.nick = nick;
        }
    }
}