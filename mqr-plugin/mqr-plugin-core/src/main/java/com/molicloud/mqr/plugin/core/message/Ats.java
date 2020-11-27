package com.molicloud.mqr.plugin.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 群里面的「@」消息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/12 2:11 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Ats {

    /**
     * 被At的群成员ID列表
     */
    private List<String> mids;

    /**
     * At群成员后面的文本
     */
    private String content;
}