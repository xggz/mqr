package com.molicloud.mqr.common.plugin.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 「分享」消息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/12 3:40 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Share {

    /**
     * 跳转的url地址
     */
    private String url;

    /**
     * 标题
     */
    private String title;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 封面图片的url地址
     */
    private String coverUrl;
}
