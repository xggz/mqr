package com.molicloud.mqr.common.plugin.action;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 禁言
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/17 10:45 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MuteAction extends Action {

    /**
     * 禁言时间（秒）
     */
    public Integer seconds;

    public MuteAction(List<String> ids, Integer seconds) {
        super.setIds(ids);
        this.seconds = seconds;
    }
}