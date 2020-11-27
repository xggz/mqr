package com.molicloud.mqr.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.molicloud.mqr.common.rest.ApiCode;
import com.molicloud.mqr.common.rest.ApiException;
import com.molicloud.mqr.entity.SysSetting;
import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.mapper.SysSettingMapper;
import com.molicloud.mqr.service.SysSettingService;
import org.springframework.beans.BeanUtils;
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
    public <T> T getSysSettingByName(SettingEnum settingEnum, Class<T> clazz) {
        LambdaQueryWrapper<SysSetting> lambdaQueryWrapper = Wrappers.<SysSetting>lambdaQuery();
        lambdaQueryWrapper.eq(SysSetting::getName, settingEnum.getName());
        lambdaQueryWrapper.last(" limit 1");
        SysSetting sysSetting = baseMapper.selectOne(lambdaQueryWrapper);
        if (sysSetting == null) {
            return null;
        }

        try {
            T object = clazz.newInstance();
            if (object instanceof String
                    || object instanceof Integer
                    || object instanceof Long
                    || object instanceof Double) {
                return (T) sysSetting.getValue();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

        JSONObject jsonObject = new JSONObject(sysSetting.getValue());
        return jsonObject.toBean(clazz);
    }

    @Override
    public <T> boolean saveSysSetting(SettingEnum settingEnum, Object dto, Class<T> clazz) {
        SysSetting sysSetting = baseMapper.selectOne(Wrappers.<SysSetting>lambdaQuery().eq(SysSetting::getName, settingEnum.getName()));
        try {
            String settingValue = "";
            T object = clazz.newInstance();
            if (sysSetting == null) {
                sysSetting = new SysSetting();
            }
            if (object instanceof String
                    || object instanceof Integer
                    || object instanceof Long
                    || object instanceof Double) {
                settingValue = String.valueOf(dto);
            } else {
                object = new JSONObject(sysSetting.getValue()).toBean(clazz);
                // 复制dto数据到目标对象
                BeanUtils.copyProperties(dto, object);
                settingValue = new JSONObject(object).toString();
            }

            // 保存配置
            sysSetting.setName(settingEnum.getName());
            sysSetting.setValue(settingValue);
            sysSetting.setRemark(settingEnum.getRemark());
            this.saveOrUpdate(sysSetting);
        } catch (Exception e) {
            throw new ApiException(ApiCode.SYSERR);
        }
        return true;
    }
}