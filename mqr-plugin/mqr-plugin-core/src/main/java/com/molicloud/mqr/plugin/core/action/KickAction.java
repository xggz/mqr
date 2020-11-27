package com.molicloud.mqr.plugin.core.action;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 踢人
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/17 10:52 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KickAction extends Action {

    public KickAction(List<String> ids) {
        setIds(ids);
    }
}