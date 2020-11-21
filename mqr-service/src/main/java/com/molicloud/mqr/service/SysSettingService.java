package com.molicloud.mqr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.molicloud.mqr.entity.SysSetting;
import com.molicloud.mqr.enums.SettingEnums;

/**
 * <p>
 * 配置表 服务类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
public interface SysSettingService extends IService<SysSetting> {

    /**
     * 获取配置信息
     *
     * @param settingEnum
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getSysSettingByName(SettingEnums settingEnum, Class<T> clazz);

    /**
     * 保存配置信息
     *
     * @param settingEnum
     * @param dto
     * @param clazz
     * @param <T>
     * @return
     */
    <T> boolean saveSysSetting(SettingEnums settingEnum, Object dto, Class<T> clazz);
}