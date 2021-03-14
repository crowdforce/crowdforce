package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import space.crowdforce.controllers.model.UserUI
import space.crowdforce.service.security.AnonymousSecurityService

/**
 * Anonymous authentication allows us to login without any external authentication providers like telegram.
 * It is useful for developer's environments.
 */
@Api(value = "/api/v1/auth/anonymous", description = "")
@RestController
@RequestMapping(
    "/api/v1/auth/anonymous",
    consumes = [MediaType.ALL_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
@Profile("default", "dev", "test") // anonymous authentication is allowed for developer's env only
class AuthAnonymousController(
    private val anonymousSecurityService: AnonymousSecurityService
) {
    @ApiOperation(
        value = "Authenticate anonymous user. " +
            "If anonymousId is provided it will try to search user for the provided id, if the user is not be found, new user will be created with the provided username" +
            "If the user is found, username will be ignored"
    )
    @GetMapping
    suspend fun authAnonymous(
        @RequestParam("anonymousId", required = false) anonymousId: String?,
        @RequestParam("username") username: String,
        serverExchange: ServerWebExchange
    ): UserUI =
        UserUI(anonymousSecurityService.authenticate(anonymousId, username, serverExchange).name)
}