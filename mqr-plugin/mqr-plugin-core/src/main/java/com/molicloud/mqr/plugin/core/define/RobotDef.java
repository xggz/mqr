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
    private List<Member> groupList;

    /**
     * 好友列表
     */
    private List<Member> friendList;

    /**
     * 群或好友信息
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Member {

        /**
         * 群ID或好友ID
         */
        private String id;

        /**
         * 群名或好友昵称
         */
        private String nick;

        /**
         * 构造函数
         *
         * @param id
         * @param nick
         */
        public Member(String id, String nick) {
            this.id = id;
            this.nick = nick;
        }
    }
}