package com.molicloud.mqr.plugin.core.define;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡片消息信息定义
 *
 * @author wispx wisp-x@qq.com
 * @since 2020/12/02 4:04 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CardDef {

    /**
     * Data
     */
    @EqualsAndHashCode(callSuper = false)
    public static class Data {

        /**
         * 标题
         */
        private String title;

        /**
         * 内容
         */
        private String value;

        /**
         * 构造函数
         *
         * @param title
         * @param value
         */
        public Data(String title, String value) {
            this.title = title;
            this.value = value;
        }
    }

    /**
     * Button
     */
    @EqualsAndHashCode(callSuper = false)
    public static class Button {

        /**
         * 名称
         */
        private String name;

        /**
         * 操作
         */
        private String action;

        /**
         * 构造函数
         *
         * @param name
         * @param action
         */
        public Button(String name, String action) {
            this.name = name;
            this.action = action;
        }
    }
}