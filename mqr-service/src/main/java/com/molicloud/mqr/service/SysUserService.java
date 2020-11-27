package com.molicloud.mqr.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.molicloud.mqr.common.dto.LoginDto;
import com.molicloud.mqr.entity.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     *
     * @param dto
     * @return
     */
    String login(LoginDto dto);
}
