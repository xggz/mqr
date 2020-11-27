package com.molicloud.mqr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.molicloud.mqr.entity.SysSetting;
import com.molicloud.mqr.common.enums.SettingEnum;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

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
    @Cacheable(value = "sysSetting", key = "#p0.name")
    <T> T getSysSettingByName(SettingEnum settingEnum, Class<T> clazz);

    /**
     * 保存配置信息
     *
     * @param settingEnum
     * @param dto
     * @param clazz
     * @param <T>
     * @return
     */
    @CacheEvict(value = "sysSetting", key = "#p0.name")
    <T> boolean saveSysSetting(SettingEnum settingEnum, Object dto, Class<T> clazz);
}