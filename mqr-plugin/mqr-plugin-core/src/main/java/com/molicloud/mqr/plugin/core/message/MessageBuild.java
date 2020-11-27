package com.molicloud.mqr.plugin.core.message;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 消息构建
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/27 4:54 下午
 */
public class MessageBuild {

    private Set<Make> makes = new LinkedHashSet<>();

    /**
     * 获取所有制作的消息
     *
     * @return
     */
    public Set<Make> getMakes() {
        return makes;
    }

    /**
     * 追加消息
     *
     * @param make
     * @return
     */
    public MessageBuild append(Make make) {
        makes.add(make);
        return this;
    }
}