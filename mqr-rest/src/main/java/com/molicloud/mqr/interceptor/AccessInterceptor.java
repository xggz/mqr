package com.molicloud.mqr.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.molicloud.mqr.common.def.CommonDef;
import com.molicloud.mqr.common.def.JwtDef;
import com.molicloud.mqr.common.rest.ApiCode;
import com.molicloud.mqr.common.rest.Res;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 访问拦截器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 11:12 上午
 */
@Slf4j
public class AccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取jwt
        String token = request.getHeader(CommonDef.ACCESS_TOKEN);

        // 如果访问token为空，则提示未登录
        if (StrUtil.isBlank(token)) {
            printResponseContent(response, Res.error(ApiCode.UNAUTHORIZED).toJsonString());
            return false;
        }

        // 验证token的合法性
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(JwtDef.HMAC256_SECRET)).build();
        DecodedJWT decodedJwt = null;
        try {
            decodedJwt = jwtVerifier.verify(token);
        } catch (Exception e) {
            printResponseContent(response, Res.error(ApiCode.UNAUTHORIZED).toJsonString());
            return false;
        }

        request.setAttribute(JwtDef.USER_ID, decodedJwt.getClaim(JwtDef.USER_ID).asInt());
        return true;
    }

    private void printResponseContent(HttpServletResponse response, String content) throws IOException {
        response.setCharacterEncoding(CommonDef.CHARSET);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.HTTP_OK);
        PrintWriter printWriter = response.getWriter();
        printWriter.println(content);
        printWriter.flush();
        printWriter.close();
    }
}
