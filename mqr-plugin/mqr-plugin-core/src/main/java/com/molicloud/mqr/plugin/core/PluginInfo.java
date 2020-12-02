package com.molicloud.mqr.plugin.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 插件信息（用于插件数据初始化和更新）
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/2 6:13 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PluginInfo {

    /**
     * 插件名字
     */
    private String name;

    /**
     * 作者
     */
    private String author;

    /**
     * 主页
     */
    private String homeUrl;

    /**
     * 说明
     */
    private String explain;

    /**
     * 当前版本的代号（需确保新版本比旧版本的值大）
     */
    private Integer version;

    /**
     * 当前版本的初始化sql脚本（初次安装时的sql脚本）
     */
    private String initScript;

    /**
     * 脚本更新列表
     *
     * key：版本代号
     * value：此版本的更新sql脚本
     *
     * 当插件升级时，程序会根据此列表来执行脚本，例：
     * 之前的版本代号是10002，本次升级的当前版本代号为10005；
     * 那么程序启动时，会更新插件版本代号为10005；
     * 并且执行版本代号在10002（不含10002）和10005（不含10005）之间的更新脚本
     */
    private Map<Integer, String> updateScriptList;
}