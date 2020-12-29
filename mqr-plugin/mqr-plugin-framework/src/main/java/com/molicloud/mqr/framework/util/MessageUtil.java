package com.molicloud.mqr.framework.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.message.MessageBuild;
import com.molicloud.mqr.plugin.core.message.make.Ats;
import com.molicloud.mqr.plugin.core.message.make.Expression;
import com.molicloud.mqr.plugin.core.message.make.Img;
import com.molicloud.mqr.plugin.core.message.make.Text;
import com.molicloud.mqr.plugin.core.message.single.Card;
import com.molicloud.mqr.plugin.core.message.single.Share;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息构建工具类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/12 2:44 下午
 */
@Slf4j
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
        } else if (pluginResultData instanceof Text) {
            return new PlainText(((Text)pluginResultData).getContent());
        } else if (pluginResultData instanceof Share) {
            return MessageUtil.buildShareMessage((Share) pluginResultData);
        } else if (pluginResultData instanceof Expression) {
            return MessageUtil.buildFaceMessage((Expression) pluginResultData);
        } else if (pluginResultData instanceof Card) {
            return MessageUtil.buildCardMessage((Card) pluginResultData);
        }
        return null;
    }

    /**
     * 转换PluginResult的data字段为QQ消息<br/>
     *
     * 处理好友或临时消息
     *
     * @param pluginResultData
     * @param personal
     * @return
     */
    public Message convertPersonalMessage(Object pluginResultData, Object personal) {
        Message message = convertMessage(pluginResultData);
        if (message == null) {
            if (pluginResultData instanceof Img) {
                return buildImageMessage(personal, ((Img) pluginResultData).getFileResource());
            } else if (pluginResultData instanceof MessageBuild) {
                return buildPersonalSrcMessage((MessageBuild) pluginResultData, personal);
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
            } else if (pluginResultData instanceof MessageBuild) {
                return buildGroupSrcMessage((MessageBuild) pluginResultData, group);
            }
        }
        return message;
    }

    /**
     * 构建插件返回的群原生消息
     *
     * @param messageBuild
     * @param group
     * @return
     */
    private MessageChain buildGroupSrcMessage(MessageBuild messageBuild, Group group) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        messageBuild.getMakes().forEach(make -> {
            if (make instanceof Text) {
                messageChainBuilder.append(((Text) make).getContent());
            } else if (make instanceof Img) {
                messageChainBuilder.append(buildImageMessage(group, ((Img) make).getFileResource()));
            } else if (make instanceof Ats) {
                messageChainBuilder.append(buildGroupAtMessage(group, (Ats) make));
            } else if (make instanceof Expression) {
                messageChainBuilder.append(buildFaceMessage((Expression) make));
            }
        });
        return messageChainBuilder.build();
    }

    /**
     * 构建插件返回的好友原生消息
     *
     * @param messageBuild
     * @param personal
     * @return
     */
    private MessageChain buildPersonalSrcMessage(MessageBuild messageBuild, Object personal) {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        messageBuild.getMakes().forEach(make -> {
            if (make instanceof Text) {
                messageChainBuilder.append(((Text) make).getContent());
            } else if (make instanceof Img) {
                messageChainBuilder.append(buildImageMessage(personal, ((Img) make).getFileResource()));
            } else if (make instanceof Expression) {
                messageChainBuilder.append(buildFaceMessage((Expression) make));
            }
        });
        return messageChainBuilder.build();
    }

    /**
     * 构建分享消息
     *
     * @param share
     * @return
     */
    private ServiceMessage buildShareMessage(Share share) {
        return RichMessage.Key.share(share.getUrl(), share.getTitle(), share.getContent(), share.getCoverUrl());
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
            ContactList<NormalMember> memberContactList = group.getMembers();
            List<NormalMember> memberList = mids.stream().map(mid -> memberContactList.get(Long.parseLong(mid))).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(memberList)) {
                List<At> atList = memberList.stream().map(member -> new At(member.getId())).collect(Collectors.toList());
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
     * @param sender Group Or Friend Or Member 对象
     * @param image
     * @return
     */
    private Image buildImageMessage(Object sender, Object image) {
        try {
            if (image instanceof File) {
                ExternalResource externalImage = ExternalResource.create((File) image);
                return sender instanceof Group ?
                        ((Group) sender).uploadImage(externalImage) :
                        (sender instanceof Member ?
                                ((Member) sender).uploadImage(externalImage) :
                                ((Friend) sender).uploadImage(externalImage));
            } else if (image instanceof InputStream) {
                ExternalResource externalImage = ExternalResource.create((InputStream) image);
                return sender instanceof Group ?
                        ((Group) sender).uploadImage(externalImage) :
                        (sender instanceof Member ?
                                ((Member) sender).uploadImage(externalImage) :
                                ((Friend) sender).uploadImage(externalImage));
            }
        } catch (Exception e) {
            log.error("文件消息转换失败", e);
        }
        return null;
    }

    /**
     * 构建表情消息
     *
     * @param expression
     * @return
     */
    private Face buildFaceMessage(Expression expression) {
        return new Face(expression.getFaceId());
    }

    /**
     * 构建卡片消息 TODO 待优化
     *
     * @param card
     * @return
     */
    private LightApp buildCardMessage(Card card) {
        JSONObject object = new JSONObject();
        JSONObject meta = new JSONObject();
        JSONObject notification = new JSONObject();
        JSONObject info = new JSONObject();

        object.putOpt("app", "com.tencent.miniapp").putOpt("desc", "").putOpt("view", "notification");
        object.putOpt("ver", "1.0.0.11").putOpt("appID", "").putOpt("prompt", card.getPrompt()).putOpt("sourceName", "");
        object.putOpt("actionData", "").putOpt("actionData_A", "").putOpt("sourceUrl", "");

        info.putOpt("appName", card.getTitle()).putOpt("appType", 4).putOpt("appid", 1109659848).putOpt("iconUrl", card.getIcon());

        notification.putOpt("appInfo", info);
        notification.putOpt("data", card.getData());
        notification.putOpt("button", card.getButton());
        notification.putOpt("emphasis_keyword", "").putOpt("title", card.getSubtitle());

        meta.putOpt("notification", notification);
        object.putOpt("meta", meta);

        return new LightApp(object.toString());
    }
}