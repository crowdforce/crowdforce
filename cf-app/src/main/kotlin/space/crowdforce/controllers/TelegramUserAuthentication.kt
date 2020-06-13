package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.crowdforce.controllers.dto.TelegramUser
import space.crowdforce.controllers.dto.VerificationCode
import space.crowdforce.service.UserLoginService

@Api(value = "/api/v1/user/telegram", description = "Telegram User Auth API")
@RestController
@RequestMapping("/api/v1/user/telegram")
class TelegramUserAuthentication(
    private val userLoginService: UserLoginService
) {

    @PostMapping("/send-code", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Send code for user login")
    suspend fun sendCodesForLogin(@RequestBody telegramUser: TelegramUser) =
        userLoginService.sendCodesToUser(userName = telegramUser.userName)

    @PostMapping("/verify", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Verify user code")
    suspend fun verifyUserCode(@RequestBody verificationCode: VerificationCode) =
        userLoginService.verifyCode(verificationCode.userName, verificationCode.code)
}
