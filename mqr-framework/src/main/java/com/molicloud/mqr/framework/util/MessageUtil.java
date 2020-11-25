package com.molicloud.mqr.framework.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.common.plugin.PluginResult;
import com.molicloud.mqr.common.plugin.message.Ats;
import com.molicloud.mqr.common.plugin.message.Img;
import com.molicloud.mqr.common.plugin.message.Share;
import lombok.experimental.UtilityClass;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.ServiceMessage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
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
     * @see PluginResult#getMessage()
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
            if (pluginResultData instanceof Img) {
                return buildImageMessage(friend, ((Img) pluginResultData).getFileResource());
            }
        }
        return message;
    }

    /**
     * 转换PluginResult的data字段为QQ消息<br/>
     *
     * 处理群消息
     *
     * @see PluginResult#getMessage()
     *
     * @param pluginResultData
     * @return
     */
    public Message convertGroupMessage(Object pluginResultData, Group group) {
        Message message = convertMessage(pluginResultData);
        if (message == null) {
            if (pluginResultData instanceof Ats) {
                return buildGroupAtMessage(group, (Ats) pluginResultData);
            } else if (pluginResultData instanceof Img) {
                return buildImageMessage(group, ((Img) pluginResultData).getFileResource());
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

    /**
     * 构建群「@」消息
     *
     * @param group
     * @param ats
     * @return
     */
    private MessageChain buildGroupAtMessage(Group group, Ats ats) {
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
        return null;
    }

    /**
     * 构建图片消息
     *
     * @param sender Group Or Friend 对象
     * @param image
     * @return
     */
    private Image buildImageMessage(Object sender, Object image) {
        if (image instanceof File) {
            return sender instanceof Group ?
                    ((Group) sender).uploadImage((File) image) :
                    ((Friend) sender).uploadImage((File) image);
        } else if (image instanceof URL) {
            return sender instanceof Group ?
                    ((Group) sender).uploadImage((URL) image) :
                    ((Friend) sender).uploadImage((URL) image);
        } else if (image instanceof InputStream) {
            return sender instanceof Group ?
                    ((Group) sender).uploadImage((InputStream) image) :
                    ((Friend) sender).uploadImage((InputStream) image);
        }
        return null;
    }
}