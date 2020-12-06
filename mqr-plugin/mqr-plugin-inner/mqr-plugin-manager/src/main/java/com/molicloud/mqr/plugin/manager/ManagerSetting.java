package com.molicloud.mqr.plugin.manager;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理配置
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/5 5:28 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ManagerSetting {

    /**
     * 是否同意自动加群
     */
    private Boolean autoJoin;

    /**
     * 是否自动发送欢迎消息
     */
    private Boolean autoWelcomeMessage;

    /**
     * 入群的欢迎消息
     */
    private String welcomeMessage;
}