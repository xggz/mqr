package com.molicloud.mqr.framework.util;

import com.molicloud.mqr.plugin.core.HoldInfo;
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
     * 机器人好友的持有信息
     */
    private static final ConcurrentHashMap<String, HoldInfo> friendHoldInfo = new ConcurrentHashMap<>();

    /**
     * 群成员的持有信息
     */
    private static final ConcurrentHashMap<String, HoldInfo> groupMemberHoldInfo = new ConcurrentHashMap<>();

    /**
     * 获取所有机器人好友的持有信息
     *
     * @return
     */
    public ConcurrentHashMap<String, HoldInfo> getAllFriendHoldInfo() {
        return friendHoldInfo;
    }

    /**
     * 根据好友ID获取插件持有信息
     *
     * @return
     */
    public HoldInfo getFriendHoldInfo(String fid) {
        return friendHoldInfo.get(fid);
    }

    /**
     * 机器人好友持有/释放插件钩子
     *
     * @param fid
     * @param name
     * @param message
     * @param action
     * @return
     */
    public boolean actionFriendPluginHook(String fid, String name, String message, boolean action) {
        boolean hasKey = friendHoldInfo.containsKey(fid);
        return handlerAction(friendHoldInfo, name, message, fid, hasKey, action);
    }

    /**
     * 机器人好友是否持有插件钩子
     *
     * @param fid
     * @return
     */
    public boolean friendHasPluginHook(String fid) {
        return friendHoldInfo.containsKey(fid);
    }

    /**
     * 获取所有群成员的持有信息
     *
     * @return
     */
    public static ConcurrentHashMap<String, HoldInfo> getAllGroupMemberHoldInfo() {
        return groupMemberHoldInfo;
    }

    /**
     * 根据群成员ID获取插件持有信息
     *
     * @return
     */
    public static HoldInfo getGroupMemberHoldInfo(String gid, String mid) {
        return groupMemberHoldInfo.get(makeGroupMemberKey(gid, mid));
    }

    /**
     * 群成员持有/释放插件钩子
     *
     * @param gid
     * @param mid
     * @param name
     * @param message
     * @param action
     * @return
     */
    public boolean actionGroupMemberPluginHook(String gid, String mid, String name, String message, boolean action) {
        String key = makeGroupMemberKey(gid, mid);
        boolean hasKey = groupMemberHoldInfo.containsKey(key);
        return handlerAction(groupMemberHoldInfo, name, message, key, hasKey, action);
    }

    /**
     * 群成员是否持有插件钩子
     *
     * @param gid
     * @param mid
     * @return
     */
    public boolean groupMemberHasPluginHook(String gid, String mid) {
        return groupMemberHoldInfo.containsKey(makeGroupMemberKey(gid, mid));
    }

    /**
     * 处理插件钩子的持有动作
     *
     * @param pluginHookMap
     * @param name
     * @param message
     * @param key
     * @param hasKey
     * @param action
     * @return
     */
    private boolean handlerAction(ConcurrentHashMap<String, HoldInfo> pluginHookMap, String name, String message, String key, boolean hasKey, boolean action) {
        if (action && (!hasKey || (hasKey && !name.equalsIgnoreCase(pluginHookMap.get(key).getHookName())))) {
            HoldInfo holdInfo = new HoldInfo();
            holdInfo.setHookName(name);
            holdInfo.setMessage(message);
            pluginHookMap.put(key, holdInfo);
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
