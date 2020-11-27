package com.molicloud.mqr.controller;

import com.molicloud.mqr.common.dto.LoginDto;
import com.molicloud.mqr.common.rest.Res;
import com.molicloud.mqr.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户相关操作接口
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 9:57 上午
 */
@RestController
@RequestMapping("/api/sys-user")
@Api(value = "UserApi", tags = "用户相关操作接口")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Res<String> login(@RequestBody @Valid LoginDto dto) {
        return Res.success(sysUserService.login(dto));
    }
}
