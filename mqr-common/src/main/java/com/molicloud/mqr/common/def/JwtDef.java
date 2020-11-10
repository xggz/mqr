package com.molicloud.mqr.common.def;

/**
 * JWT相关常量定义
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 3:28 下午
 */
public interface JwtDef {

    /**
     * 密钥
     */
    String HMAC256_SECRET = "mqr2020";

    /**
     * jwt过期时间
     */
    Long EXPIRES_TIME = 24 * 60 * 60 * 1000L;

    /**
     * jwt中的用户ID字段
     */
    String USER_ID = "id";

    /**
     * jwt中的用户名字段
     */
    String USER_NAME = "username";

    /**
     * jwt中的用户头像字段
     */
    String USER_AVATAR = "avatar";
}
