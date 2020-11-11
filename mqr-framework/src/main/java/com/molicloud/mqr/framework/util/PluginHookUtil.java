package com.molicloud.mqr.framework.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件钩子工具类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/11 11:22 上午
 */
@UtilityClass
public class PluginHookUtil {

    /**
     * 机器人好友持有的插件钩子
     */
    private static final ConcurrentHashMap<String, String> friendHoldPluginHook = new ConcurrentHashMap<>();

    /**
     * 群成员持有的插件钩子
     */
    private static final ConcurrentHashMap<String, String> groupMemberHoldPluginHook = new ConcurrentHashMap<>();

    /**
     * 获取所有机器人好友持有的插件钩子
     *
     * @return
     */
    public ConcurrentHashMap<String, String> getAllFriendHoldPluginHook() {
        return friendHoldPluginHook;
    }

    /**
     * 获取所有群成员持有的插件钩子
     *
     * @return
     */
    public static ConcurrentHashMap<String, String> getAllGroupMemberHoldPluginHook() {
        return groupMemberHoldPluginHook;
    }

    /**
     * 根据好友ID获取插件钩子名
     *
     * @return
     */
    public String getFriendHoldPluginHookName(String fid) {
        return friendHoldPluginHook.get(fid);
    }

    /**
     * 根据群成员ID获取插件钩子名
     *
     * @return
     */
    public static String getGroupMemberHoldPluginHookName(String gid, String mid) {
        return groupMemberHoldPluginHook.get(makeGroupMemberKey(gid, mid));
    }

    /**
     * 机器人好友释放插件钩子
     *
     * @param fid
     */
    public void releaseFriendPluginHook(String fid) {
        friendHoldPluginHook.remove(fid);
    }

    /**
     * 群成员释放插件钩子
     *
     * @param gid
     * @param mid
     */
    public void releaseGroupMemberPluginHook(String gid, String mid) {
        groupMemberHoldPluginHook.remove(makeGroupMemberKey(gid, mid));
    }

    /**
     * 机器人好友持有插件钩子
     *
     * @param fid
     * @param name
     */
    public void holdFriendPluginHook(String fid, String name) {
        friendHoldPluginHook.put(fid, name);
    }

    /**
     * 群成员持有插件钩子
     *
     * @param gid
     * @param mid
     * @param name
     */
    public void holdGroupMemberPluginHook(String gid, String mid, String name) {
        groupMemberHoldPluginHook.put(makeGroupMemberKey(gid, mid), name);
    }

    /**
     * 机器人好友是否持有插件钩子
     *
     * @param fid
     * @return
     */
    public boolean friendHasPluginHook(String fid) {
        return friendHoldPluginHook.containsKey(fid);
    }

    /**
     * 群成员是否持有插件钩子
     *
     * @param gid
     * @param mid
     * @return
     */
    public boolean groupMemberHasPluginHook(String gid, String mid) {
        return groupMemberHoldPluginHook.containsKey(makeGroupMemberKey(gid, mid));
    }

    /**
     * 制作群成员的插件钩子Key
     *
     * @param gid
     * @param mid
     * @return
     */
    public String makeGroupMemberKey(String gid, String mid) {
        return gid.concat("-").concat(mid);
    }
}
