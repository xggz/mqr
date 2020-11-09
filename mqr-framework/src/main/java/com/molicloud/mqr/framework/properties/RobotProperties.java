package com.molicloud.mqr.framework.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 机器人QQ信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 4:03 下午
 */
@Data
@Component
@ConfigurationProperties("robot")
public class RobotProperties {

    /**
     * 机器人QQ号码
     */
    private Long qq;

    /**
     * 机器人QQ密码
     */
    private String password;
}
