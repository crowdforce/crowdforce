package space.crowdforce.service.security

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.apache.http.auth.BasicUserPrincipal
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import space.crowdforce.domain.User
import space.crowdforce.domain.UserIdentity
import space.crowdforce.service.user.UserService
import java.util.UUID

@Service
@Lazy
@Profile("default", "dev", "test") // anonymous authentication is allowed for developer's env only
class AnonymousSecurityService(
    private val serverSecurityContextRepository: ServerSecurityContextRepository,
    private val userService: UserService
) {

    suspend fun authenticate(
        anonymousId: String?,
        username: String,
        serverExchange: ServerWebExchange
    ): User {
        val user = userService.getOrCreateUser(
            UserIdentity.ANON.identityKey(anonymousId ?: UUID.randomUUID().toString()),
            username
        )

        val token = UsernamePasswordAuthenticationToken(
            BasicUserPrincipal(user.id.toString()),
            Object()
        )

        serverSecurityContextRepository
            .save(serverExchange, SecurityContextImpl(token))
            .subscriberContext(ReactiveSecurityContextHolder.withAuthentication(token))
            .awaitFirstOrNull()

        return user
    }
}