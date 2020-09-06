package space.crowdforce.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun serverSecurityContextRepository() = WebSessionServerSecurityContextRepository()

    @Bean
    fun securitygWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authorizeExchange()
            .pathMatchers("/**").permitAll()
            .anyExchange().authenticated()

        http.cors()
            .and()
            .csrf().disable() // csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
