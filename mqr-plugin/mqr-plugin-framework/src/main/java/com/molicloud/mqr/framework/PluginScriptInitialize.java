package com.molicloud.mqr.framework;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.entity.RobotPlugin;
import com.molicloud.mqr.plugin.core.PluginExecutor;
import com.molicloud.mqr.plugin.core.PluginInfo;
import com.molicloud.mqr.service.CommonService;
import com.molicloud.mqr.service.RobotPluginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 插件脚本初始化
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/2 3:57 下午
 */
@Slf4j
@Component
public class PluginScriptInitialize implements CommandLineRunner, Ordered {

    @Autowired
    private CommonService commonService;

    @Autowired
    private RobotPluginService robotPluginService;

    @Autowired
    private List<PluginExecutor> pluginExecutorList;

    @Override
    public void run(String... args) throws Exception {
        // 获取目前的所有插件记录
        List<RobotPlugin> robotPluginList = robotPluginService.list();
        // 待新增插件列表
        List<RobotPlugin> insertList = new ArrayList<>();
        // 待更新插件列表
        List<RobotPlugin> updateList = new ArrayList<>();
        // 待执行sql列表
        List<String> sqlList = new ArrayList<>();
        // 扫描到的插件
        pluginExecutorList.stream().filter(pluginExecutor -> pluginExecutor.getPluginInfo() != null).forEach(pluginExecutor -> {
            PluginInfo pluginInfo = pluginExecutor.getPluginInfo();
            RobotPlugin robotPlugin = robotPluginList.stream()
                    .filter(rp -> pluginExecutor.getClass().getName().equals(rp.getClassName()))
                    .findAny().orElse(null);
            if (robotPlugin == null) {
                robotPlugin = new RobotPlugin();
                robotPlugin.setClassName(pluginExecutor.getClass().getName());
                BeanUtils.copyProperties(pluginInfo, robotPlugin);
                insertList.add(robotPlugin);
                if (StrUtil.isNotBlank(pluginInfo.getInitScript())) {
                    sqlList.add(pluginInfo.getInitScript());
                }
            } else {
                // 数据库里的插件版本
                final Integer version = robotPlugin.getVersion();
                if (pluginInfo.getVersion() > version) {
                    BeanUtils.copyProperties(pluginInfo, robotPlugin);
                    robotPlugin.setUpdateTime(new Date());
                    updateList.add(robotPlugin);
                    if (pluginInfo.getUpdateScriptList() != null) {
                        pluginInfo.getUpdateScriptList().forEach((ver, sql) -> {
                            if (version > ver && StrUtil.isNotBlank(sql)) {
                                sqlList.add(sql);
                            }
                        });
                    }
                }
            }
        });

        // 执行所有的sql脚本（初始化或更新）
        sqlList.stream().forEach(sql -> {
            commonService.executeScript(sql);
        });

        // 保存新增的插件信息
        if (CollUtil.isNotEmpty(insertList)) {
            robotPluginService.saveBatch(insertList);
        }
        // 修改更新的插件信息
        if (CollUtil.isNotEmpty(updateList)) {
            robotPluginService.updateBatchById(updateList);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}