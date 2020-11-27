package com.molicloud.mqr.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.molicloud.mqr.common.def.JwtDef;
import com.molicloud.mqr.common.rest.ApiCode;
import com.molicloud.mqr.common.rest.ApiException;
import com.molicloud.mqr.common.dto.LoginDto;
import com.molicloud.mqr.entity.SysUser;
import com.molicloud.mqr.mapper.SysUserMapper;
import com.molicloud.mqr.service.SysUserService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author feitao
 * @since 2020-11-07
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public String login(LoginDto dto) {
        SysUser sysUser = baseMapper.selectOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, dto.getUsername()));
        if (sysUser == null || !sysUser.getPassword().equalsIgnoreCase(SecureUtil.md5(dto.getPassword().concat(sysUser.getSalt())))) {
            throw new ApiException(ApiCode.FAILED, "用户名不存在或密码错误");
        }

        return JWT.create()
                .withClaim(JwtDef.USER_ID, sysUser.getId())
                .withClaim(JwtDef.USER_NAME, sysUser.getUsername())
                .withClaim(JwtDef.USER_AVATAR, sysUser.getAvatar())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtDef.EXPIRES_TIME))
                .sign(Algorithm.HMAC256(JwtDef.HMAC256_SECRET));
    }
}
