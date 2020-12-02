package com.molicloud.mqr.service;

/**
 * 公共 服务类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/2 8:46 下午
 */
public interface CommonService {

    /**
     * 执行Sql脚本
     *
     * @param sql
     */
    void executeScript(String sql);
}