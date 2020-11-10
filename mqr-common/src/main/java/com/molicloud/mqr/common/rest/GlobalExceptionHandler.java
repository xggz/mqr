package com.molicloud.mqr.common.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/10 3:11 下午
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ApiException.class)
    public Res handlerAppException(ApiException e) {
        log.error(e.getMessage());
        return new Res(e.getAidCode().getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Res handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (message.length() > 0) {
                message.append("、");
            }
            message.append(error.getDefaultMessage());
        }
        return Res.failed(message.toString());
    }

    /**
     * 系统异常捕获
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Res handleGlobalException(Exception e) {
        log.error("系统异常信息：{}", e.getMessage(), e);
        return Res.error(ApiCode.SYSERR);
    }
}
