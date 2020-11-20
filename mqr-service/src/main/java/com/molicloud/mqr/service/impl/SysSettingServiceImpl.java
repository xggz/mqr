package com.molicloud.mqr.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.molicloud.mqr.entity.SysSetting;
import com.molicloud.mqr.mapper.SysSettingMapper;
import com.molicloud.mqr.service.SysSettingService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 配置表 服务实现类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
@Service
public class SysSettingServiceImpl extends ServiceImpl<SysSettingMapper, SysSetting> implements SysSettingService {

    @Override
    public <T> T getSysSettingByName(String name, Class<T> clazz) {
        LambdaQueryWrapper<SysSetting> lambdaQueryWrapper = Wrappers.<SysSetting>lambdaQuery();
        lambdaQueryWrapper.eq(SysSetting::getName, name);
        lambdaQueryWrapper.last(" limit 1");
        SysSetting sysSetting = baseMapper.selectOne(lambdaQueryWrapper);
        if (sysSetting == null) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(sysSetting.getValue());
        return jsonObject.toBean(clazz);
    }
}