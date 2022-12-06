package com.molicloud.mqr.framework.handler

import com.molicloud.mqr.common.enums.RobotVerifyEnum
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.DeviceVerificationRequests
import net.mamoe.mirai.utils.DeviceVerificationResult
import net.mamoe.mirai.utils.LoginSolver
import javax.annotation.Resource

/**
 * 机器人登录抽象验证处理
 *
 * @author xggz yyimba@qq.com
 * @date 2022/11/25 10:23
 */
abstract class AbstractLoginVerifyHandler : LoginSolver() {

    @Resource
    lateinit var loginVerfyService: LoginVerfyService

    /**
     * 处理设备验证
     */
    override suspend fun onSolveDeviceVerification(
        bot: Bot,
        requests: DeviceVerificationRequests
    ): DeviceVerificationResult {
        val smsRequest = requests.sms
        val fallbackRequest = requests.fallback
        if (requests.preferSms || fallbackRequest == null) {
            if (smsRequest != null) {
                smsRequest.requestSms()
                val smsCode = loginVerfyService.handlerVerify(RobotVerifyEnum.SMS, smsRequest.countryCode + "-" + smsRequest.phoneNumber)
                return smsRequest.solved(smsCode)
            }
        } else {
            loginVerfyService.handlerVerify(RobotVerifyEnum.URL, fallbackRequest.url)
            return fallbackRequest.solved()
        }
        
        throw UnsupportedOperationException("不支持该验证方式")
    }
}