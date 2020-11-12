package com.molicloud.mqr.framework.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.common.PluginResult;
import com.molicloud.mqr.common.message.Ats;
import com.molicloud.mqr.common.message.Share;
import lombok.experimental.UtilityClass;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.ServiceMessage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息构建工具类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/12 2:44 下午
 */
@UtilityClass
public class MessageUtil {

    /**
     * 转换PluginResult的data字段为QQ消息<br/>
     *
     * 仅处理对话消息类型的data
     *
     * @see PluginResult#getData()
     *
     * @param pluginResultData
     * @return
     */
    public Message convertMessage(Object pluginResultData) {
        if (pluginResultData instanceof String) {
            return new PlainText(String.valueOf(pluginResultData));
        } else if (pluginResultData instanceof Share) {
            return MessageUtil.buildShareMessage((Share) pluginResultData);
        }
        return null;
    }

    /**
     * 转换PluginResult的data字段为QQ消息<br/>
     *
     * 处理好友消息
     *
     * @param pluginResultData
     * @param friend
     * @return
     */
    public Message convertFriendMessage(Object pluginResultData, Friend friend) {
        Message message = convertMessage(pluginResultData);
        if (message == null) {
            // todo 待续
        }
        return message;
    }

    /**
     * 转换PluginResult的data字段为QQ消息<br/>
     *
     * 处理群消息
     *
     * @see PluginResult#getData()
     *
     * @param pluginResultData
     * @return
     */
    public Message convertGroupMessage(Object pluginResultData, Group group) {
        Message message = convertMessage(pluginResultData);
        if (message == null) {
            if (pluginResultData instanceof Ats) {
                Ats ats = (Ats) pluginResultData;
                List<String> mids = ats.getMids();
                if (CollUtil.isNotEmpty(mids)) {
                    ContactList<Member> memberContactList = group.getMembers();
                    List<Member> memberList = mids.stream().map(mid -> memberContactList.get(Long.parseLong(mid))).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(memberList)) {
                        List<At> atList = memberList.stream().map(member -> new At(member)).collect(Collectors.toList());
                        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                        atList.stream().forEach(at -> messageChainBuilder.append(at).append(" "));
                        if (StrUtil.isNotBlank(ats.getContent())) {
                            messageChainBuilder.append(ats.getContent());
                        }
                        return messageChainBuilder.build();
                    }
                }
            }
        }
        return message;
    }

    /**
     * 构建分享消息
     *
     * @param share
     * @return
     */
    private ServiceMessage buildShareMessage(Share share) {
        return ServiceMessage.Templates.share(share.getUrl(), share.getTitle(), share.getContent(), share.getCoverUrl());
    }
}