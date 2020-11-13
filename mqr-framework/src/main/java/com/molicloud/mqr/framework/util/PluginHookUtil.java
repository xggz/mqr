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
     * 根据好友ID获取插件钩子名
     *
     * @return
     */
    public String getFriendHoldPluginHookName(String fid) {
        return friendHoldPluginHook.get(fid);
    }

    /**
     * 机器人好友持有/释放插件钩子
     *
     * @param fid
     * @param name
     * @param action
     * @return
     */
    public boolean actionFriendPluginHook(String fid, String name, boolean action) {
        boolean hasKey = friendHoldPluginHook.containsKey(fid);
        return handlerAction(friendHoldPluginHook, name, fid, hasKey, action);
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
     * 获取所有群成员持有的插件钩子
     *
     * @return
     */
    public static ConcurrentHashMap<String, String> getAllGroupMemberHoldPluginHook() {
        return groupMemberHoldPluginHook;
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
     * 群成员持有/释放插件钩子
     *
     * @param gid
     * @param mid
     * @param name
     * @param action
     * @return
     */
    public boolean actionGroupMemberPluginHook(String gid, String mid, String name, boolean action) {
        String key = makeGroupMemberKey(gid, mid);
        boolean hasKey = groupMemberHoldPluginHook.containsKey(key);
        return handlerAction(groupMemberHoldPluginHook, name, key, hasKey, action);
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
     * 处理插件钩子的持有动作
     *
     * @param pluginHookMap
     * @param name
     * @param key
     * @param hasKey
     * @param action
     * @return
     */
    private boolean handlerAction(ConcurrentHashMap<String, String> pluginHookMap, String name, String key, boolean hasKey, boolean action) {
        if (action && (!hasKey || (hasKey && !name.equalsIgnoreCase(pluginHookMap.get(key))))) {
            pluginHookMap.put(key, name);
            return true;
        } else if (!action && hasKey) {
            pluginHookMap.remove(key);
            return true;
        }
        return false;
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
