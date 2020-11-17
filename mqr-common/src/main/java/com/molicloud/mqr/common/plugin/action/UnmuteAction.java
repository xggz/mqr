package com.molicloud.mqr.common.plugin.action;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 解除禁言
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/17 10:51 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnmuteAction extends Action {

    public UnmuteAction(List<String> ids) {
        setIds(ids);
    }
}