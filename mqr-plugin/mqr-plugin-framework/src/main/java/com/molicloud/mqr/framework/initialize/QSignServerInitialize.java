package com.molicloud.mqr.framework.initialize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import top.mrxiaom.qsign.QSignService;

import java.io.File;

/**
 * QQ签名服务初始化
 *
 * @author xggz 2023-08-19
 */
@Slf4j
@Component
public class QSignServerInitialize implements CommandLineRunner, Ordered {

    @Override
    public void run(String... args) throws Exception {
        QSignService.Factory.init(new File("android-sign"));
        QSignService.Factory.loadProtocols(null);
        QSignService.Factory.register();
        log.debug("QQ签名服务初始化完成");
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
