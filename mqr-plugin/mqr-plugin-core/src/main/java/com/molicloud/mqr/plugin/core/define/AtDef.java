package com.molicloud.mqr.plugin.core.define;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * At「@」信息定义
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/28 10:17 上午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AtDef {

    /**
     * 被At的ID
     */
    private String id;

    /**
     * 被At的昵称
     */
    private String nick;
}