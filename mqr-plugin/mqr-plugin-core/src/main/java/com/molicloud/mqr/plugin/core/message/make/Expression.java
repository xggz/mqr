package com.molicloud.mqr.plugin.core.message.make;

import com.molicloud.mqr.plugin.core.message.Make;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 「原生表情」消息
 *
 * @author wisp-x wisp-x@qq.com
 * @since 2020/11/27 2:52 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Expression extends Make {

    /**
     * 表情ID
     *
     * @see com.molicloud.mqr.plugin.core.define.FaceDef
     */
    private int faceId;

    /**
     * 构造函数
     *
     * @param faceId
     */
    public Expression(int faceId) {
        this.faceId = faceId;
    }
}