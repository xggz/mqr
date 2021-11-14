package com.molicloud.mqr.plugin.aireply;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回复配置
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/11 3:10 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AiRepltSetting {

    /**
     * 聊天前缀
     */
    private String prefix;

    /**
     * 是否@消息才回复
     */
    private Boolean atReply;

    /**
     * 是否万金油回复
     */
    private Boolean randomReply;

    /**
     * apiKey（从 http://aichat.itpk.cn 获取）
     */
    private String apiKey;

    /**
     * apiSecret（从 http://aichat.itpk.cn 获取）
     */
    private String apiSecret;

    /**
     * 报时类型（1：所有群，2：仅白名单内的群，3：关闭报时）
     */
    private Integer timerType;

    /**
     * 报时者名字
     */
    private String timerName;
}