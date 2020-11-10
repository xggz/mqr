package com.molicloud.mqr.common.rest;

import java.text.MessageFormat;

/**
 * Api 服务内部异常
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 3:10 下午
 */
public class ApiException extends RuntimeException {

    private ApiCode code;

    public ApiException(ApiCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public ApiException(ApiCode code, Object... obj) {
        super(MessageFormat.format(code.getMessage(), obj));
        this.code = code;
    }

    public ApiCode getAidCode() {
        return code;
    }

    public void setApiCode(ApiCode code) {
        this.code = code;
    }
}
