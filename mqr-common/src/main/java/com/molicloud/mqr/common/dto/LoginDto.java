package com.molicloud.mqr.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户登录入参
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 2:24 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "登录用户名不能为空")
    private String username;

    @NotBlank(message = "登录密码不能为空")
    private String password;
}
