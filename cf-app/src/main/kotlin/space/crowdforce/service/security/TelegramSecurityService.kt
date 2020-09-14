package space.crowdforce.service.security

import org.apache.commons.codec.binary.Hex
import org.apache.http.auth.BasicUserPrincipal
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import space.crowdforce.service.user.UserService
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.stream.Collectors
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class TelegramSecurityService(
    private val serverSecurityContextRepository: ServerSecurityContextRepository,
    private val userService: UserService,
    @Value("\${telegram.bot.token}") private val tgBotToken: String
) {
    fun authenticate(
        telegramId: Int,
        hash: String,
        authDate: Long,
        queryParams: Map<String, String>,
        severExchange: ServerWebExchange
    ): Mono<Void> {
        /* todo if(authDate < System.currentTimeMillis())*/

        val validToken = buildToken(queryParams)

        if (validToken == hash) {
            userService.getOrCreateUser(telegramId, name(queryParams))

            val token = UsernamePasswordAuthenticationToken(
                BasicUserPrincipal(telegramId.toString()),
                Object()
            )

            return serverSecurityContextRepository.save(severExchange, SecurityContextImpl(token))
                .subscriberContext(ReactiveSecurityContextHolder.withAuthentication(token))
        }

        throw BadCredentialsException("Wrong token [$hash].")
    }

    private fun name(queryParams: Map<String, String>): String = queryParams.get("username").let {
        queryParams.get("first_name")
    }.let {
        queryParams.get("id")!!
    }

    private fun buildToken(queryParams: Map<String, String>): String {
        val str: String = queryParams.entries.stream()
            .filter { it.key != "hash" && it.key != "redirectTo" }
            .sorted { a: Map.Entry<String, Any>, b: Map.Entry<String, Any> -> a.key.compareTo(b.key) }
            .map { kvp: Map.Entry<String, Any> -> kvp.key + "=" + kvp.value }
            .collect(Collectors.joining("\n"))

        val sk = SecretKeySpec( // Get SHA 256 from telegram token
            MessageDigest.getInstance("SHA-256").digest(tgBotToken.toByteArray(StandardCharsets.UTF_8)
            ), "HmacSHA256")
        val mac: Mac = Mac.getInstance("HmacSHA256")
        mac.init(sk)
        val result: ByteArray = mac.doFinal(str.toByteArray(StandardCharsets.UTF_8))

        return Hex.encodeHexString(result)
    }
}