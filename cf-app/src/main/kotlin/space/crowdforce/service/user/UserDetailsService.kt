package space.crowdforce.service.user

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import space.crowdforce.repository.UserCodesRepository
import space.crowdforce.repository.UserRepository

/**
 * The part of spring security framework, makes authentication via telegram codes.
 */
@Component
class UserDetailsService(
    private val userCodesRepository: UserCodesRepository,
    private val userRepository: UserRepository,
    @Value("\${security.auth.expiration-code-time-seconds}") var expirationCodeTimeSeconds: Long
) : ReactiveUserDetailsService {
    /**
     * Find the UserDetails by username.
     *
     * @param userName telegram user name.
     *
     * @return Returns UserDetail if user and active code exist.
     */
    @Transactional
    override fun findByUsername(userName: String): Mono<UserDetails> =
        Mono.justOrEmpty(userRepository.findByUserName(userName))
            .flatMap { Mono.justOrEmpty(userCodesRepository.getActiveUserCode(it.id, expirationCodeTimeSeconds)) }
            .map { User(userName, it, listOf(SimpleGrantedAuthority("default"))) }
}
