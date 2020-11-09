package com.molicloud.mqr.framework.common;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

/**
 * 机器人终端设备信息
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 6:12 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private byte[] display = "MOLI.".concat(RandomUtil.randomNumbers(6)).concat(".001").getBytes();

    private byte[] product = "Moli".getBytes();

    private byte[] device = "Moli".getBytes();

    private byte[] board = "Moli".getBytes();

    private byte[] brand = "molicloud".getBytes();

    private byte[] model = "MQR".getBytes();

    private byte[] bootloader = "Moli".getBytes();

    private byte[] fingerprint = "molicloud/Moli/Moli:10/MOLI.200122.001/".concat(RandomUtil.randomNumbers(7)).concat(":user/release-keys").getBytes();;

    private byte[] bootId = UUID.randomUUID().toString().toUpperCase().getBytes();

    private byte[] procVersion = "Linux version 3.0.31-".concat(RandomUtil.randomString(8)).concat(" (android-build@xxx.xxx.xxx.xxx.com)").getBytes();

    private byte[] baseBand = new byte[0];

    private Version version = new Version();

    private byte[] simInfo = "T-Mobile".getBytes();

    private byte[] osType = "android".getBytes();

    private byte[] macAddress = "02:00:00:00:00:00".getBytes();

    private byte[] wifiBSSID = "02:00:00:00:00:00".getBytes();

    private byte[] wifiSSID = "<unknown ssid>".getBytes();

    private byte[] imsiMd5 = SecureUtil.md5().digest(RandomUtil.randomBytes(16));

    private String imei = RandomUtil.randomNumbers(15);

    private byte[] apn = "wifi".getBytes();

    @Data
    @EqualsAndHashCode(callSuper = false)
    private static class Version implements Serializable {
        private byte[] incremental = "5891938".getBytes();
        private byte[] release = "10".getBytes();
        private byte[] codename = "REL".getBytes();
        private int sdk = 29;
    }
}
