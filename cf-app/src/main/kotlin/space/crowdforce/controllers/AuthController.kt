package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import space.crowdforce.controllers.model.TelegramUserUI
import space.crowdforce.controllers.model.UserUI
import space.crowdforce.service.user.UserService
import java.security.Principal

@Api(value = "/api/v1/auth", description = "")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val userService: UserService) {
    @PostMapping("/send-code", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Send code for user login")
    suspend fun sendCodesForLogin(@RequestBody telegramUser: TelegramUserUI) =
            userService.sendCodesToUser(userName = telegramUser.userName)

    @GetMapping("/user", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Return current user")
    @ResponseBody
    suspend fun currentUser(principal: Principal?) =
            UserUI(principal?.name)
}