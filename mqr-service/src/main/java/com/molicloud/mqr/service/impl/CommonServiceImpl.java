package com.molicloud.mqr.service.impl;

import com.molicloud.mqr.mapper.CommonMapper;
import com.molicloud.mqr.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 公共 服务实现类
 *
 * @author feitao yyimba@qq.com
 * @since 2020/12/2 8:47 下午
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public void executeScript(String sql) {
        commonMapper.executeScript(sql);
    }
}