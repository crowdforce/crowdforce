package space.crowdforce.controllers

import dev.whyoleg.ktd.TelegramClient
import dev.whyoleg.ktd.api.authentication.setAuthenticationPhoneNumber
import dev.whyoleg.ktd.api.check.checkAuthenticationCode
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.crowdforce.controllers.dto.AuthenticationCode
import space.crowdforce.controllers.dto.AuthenticationNumber

@Api(value = "/", description = "Telegram Amin Auth API")
@RestController
@RequestMapping("/api/v1/admin/telegram")
class TelegramAdminAuth(
    val client: TelegramClient
) {

    @PostMapping("/start", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Start auth process for telegram")
    suspend fun setAuthenticationPhoneNumber(
        @RequestBody
        authenticationNumber: AuthenticationNumber
    ) =
        client.setAuthenticationPhoneNumber(
            phoneNumber = authenticationNumber.phoneNumber
        )

    @PostMapping("/complete", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Complete auth process for telegram")
    suspend fun checkAuthenticationCode(
        @RequestBody
        authenticationCode: AuthenticationCode
    ) =
        client.checkAuthenticationCode(
            code = authenticationCode.code
        )
}
