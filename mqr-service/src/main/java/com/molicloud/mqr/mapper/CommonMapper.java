package com.molicloud.mqr.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 公共 Mapper 接口
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/2 8:48 下午
 */
@Mapper
public interface CommonMapper {

    /**
     * 执行Sql脚本
     *
     * @param sql
     */
    int executeScript(@Param("sql") String sql);
}