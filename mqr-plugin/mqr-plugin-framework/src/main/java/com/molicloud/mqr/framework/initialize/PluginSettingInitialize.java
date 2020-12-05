package com.molicloud.mqr.framework.initialize;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.molicloud.mqr.common.enums.SettingTypeEnum;
import com.molicloud.mqr.entity.SysSetting;
import com.molicloud.mqr.service.SysSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 初始化插件配置
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/3 13:46 下午
 */
@Slf4j
@Component
public class PluginSettingInitialize implements CommandLineRunner, Ordered {

    @Autowired
    private SysSettingService sysSettingService;

    // 插件配置集合
    private static final Map<String, String> pluginSettingMap = new HashMap<>();

    /**
     * 重置插件的配置值
     *
     * @param name
     * @param value
     */
    public static void putPluginSettingValue(String name, String value) {
        pluginSettingMap.put(name, value);
    }

    /**
     * 根据名字获取插件的配置信息
     *
     * @param name
     * @return
     */
    public static String getPluginSettingValue(String name) {
        return pluginSettingMap.get(name);
    }

    @Override
    public void run(String... args) throws Exception {
        List<SysSetting> sysSettings = sysSettingService.list(Wrappers.<SysSetting>lambdaQuery()
                .eq(SysSetting::getType, SettingTypeEnum.PLUGIN.getValue()));
        if (CollUtil.isNotEmpty(sysSettings)) {
            Map<String, String> sysSettingMap = sysSettings.stream().collect(Collectors.toMap(SysSetting::getName, sysSetting -> sysSetting.getValue(), (key1, key2) -> key2));
            pluginSettingMap.putAll(sysSettingMap);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}