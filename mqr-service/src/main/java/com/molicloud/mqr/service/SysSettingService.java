package com.molicloud.mqr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.molicloud.mqr.entity.SysSetting;

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
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getSysSettingByName(String name, Class<T> clazz);

}
