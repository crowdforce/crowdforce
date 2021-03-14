package space.crowdforce

import org.apache.http.auth.BasicUserPrincipal
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import space.crowdforce.service.user.UserService
import javax.inject.Inject

class WithMockUserIdentitySecurityContextFactory : WithSecurityContextFactory<WithMockUserIdentity> {
    @Inject
    private lateinit var userService: UserService

    override fun createSecurityContext(annotation: WithMockUserIdentity): SecurityContext {
        val securityContext = SecurityContextHolder.createEmptyContext()
        val user = userService.getOrCreateUser(annotation.userIdentity.identityKey(annotation.userIdentityId), annotation.username)
        val token = UsernamePasswordAuthenticationToken(
            BasicUserPrincipal(user.id.toString()),
            Object()
        )
        securityContext.authentication = token
        return securityContext
    }
}