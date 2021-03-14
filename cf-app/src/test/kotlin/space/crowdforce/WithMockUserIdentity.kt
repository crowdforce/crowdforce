package space.crowdforce

import org.springframework.security.test.context.support.WithSecurityContext
import space.crowdforce.domain.UserIdentity

/**
 * Mock security context for the specified user identity.
 * It automatically creates user for the specified identity and put it in the security context as authentication token.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockUserIdentitySecurityContextFactory::class)
annotation class WithMockUserIdentity(
    val userIdentity: UserIdentity,
    val userIdentityId: String,
    val username: String
)
