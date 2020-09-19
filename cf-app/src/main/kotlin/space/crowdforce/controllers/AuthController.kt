package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType.ALL_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import space.crowdforce.controllers.model.UserUI
import space.crowdforce.domain.User
import space.crowdforce.service.security.TelegramSecurityService
import space.crowdforce.service.user.UserService
import java.security.Principal
import java.util.stream.Collectors

@Api(value = "/api/v1/auth", description = "")
@RestController
@RequestMapping("/api/v1/auth", consumes = [ALL_VALUE], produces = [APPLICATION_JSON_VALUE])
class AuthController(
    private val securityService: TelegramSecurityService,
    private val userService: UserService
) {
    @GetMapping("/user")
    @ApiOperation(value = "Return current user")
    suspend fun currentUser(principal: Principal?) =
        principal?.let { map(userService.getUserByTelegramId(it.name.toInt())!!) }

    @GetMapping
    fun auth(
        @RequestParam("id") id: Int,
        @RequestParam("hash") hash: String,
        @RequestParam("redirect_to") redirectTo: String,
        @RequestParam("auth_date") authDate: Long,
        severExchange: ServerWebExchange
    ): Mono<String> =
        securityService.authenticate(id, hash, authDate, toSingleMap(severExchange.request.queryParams), severExchange)
            .then(Mono.just("redirect:$redirectTo"))

    fun toSingleMap(queryParams: MultiValueMap<String, String>): Map<String, String> =
        queryParams.entries.stream().collect(Collectors.toMap({ it.key }, { it.value.first() }))

    fun map(user: User): UserUI = UserUI(user.name)
}
