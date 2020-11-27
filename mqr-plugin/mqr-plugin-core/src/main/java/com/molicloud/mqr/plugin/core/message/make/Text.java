package com.molicloud.mqr.plugin.core.message.make;

import com.molicloud.mqr.plugin.core.message.Make;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文本字符串消息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/27 5:35 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Text extends Make {

    /**
     * 文本消息内容
     */
    private String content;

    /**
     * 构造函数
     *
     * @param content
     */
    public Text(String content) {
        this.content = content;
    }
}