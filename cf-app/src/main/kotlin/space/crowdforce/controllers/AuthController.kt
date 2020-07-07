package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import space.crowdforce.controllers.model.TelegramUserUI
import space.crowdforce.controllers.model.UserUI
import space.crowdforce.service.user.UserService
import java.security.Principal

@Api(value = "/api/v1/auth", description = "")
@RestController
@RequestMapping("/api/v1/auth", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
class AuthController(private val userService: UserService) {
    @PostMapping("/send-code")
    @ApiOperation(value = "Send code for user login")
    suspend fun sendCodesForLogin(@RequestBody telegramUser: TelegramUserUI) =
        userService.sendCodesToUser(userName = telegramUser.userName)

    @GetMapping("/user")
    @ApiOperation(value = "Return current user")
    suspend fun currentUser(principal: Principal?) =
        UserUI(principal?.name)
}
