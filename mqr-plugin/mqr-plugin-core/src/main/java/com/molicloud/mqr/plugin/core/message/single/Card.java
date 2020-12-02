package com.molicloud.mqr.plugin.core.message.single;

import com.molicloud.mqr.plugin.core.define.CardDef;
import com.molicloud.mqr.plugin.core.message.Single;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 「卡片」消息
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/12/02 10:53 上午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Card extends Single {

    /**
     * 卡片标题
     */
    String title = "茉莉机器人";

    /**
     * 子标题
     */
    String subtitle = "";

    /**
     * 卡片 Icon 图标地址
     */
    String icon = "http://mqr.molicloud.com/hero.jpg";

    /**
     * 窗口消息通知文字
     */
    String prompt = "有新的消息通知";

    /**
     * 卡片 data 数据
     *
     * @see com.molicloud.mqr.plugin.core.define.CardDef.Data
     */
    List<CardDef.Data> data;

    /**
     * 卡片 button 数据
     *
     * @see com.molicloud.mqr.plugin.core.define.CardDef.Button
     */
    List<CardDef.Button> button;
}
