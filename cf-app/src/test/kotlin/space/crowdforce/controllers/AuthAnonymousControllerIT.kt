package space.crowdforce.controllers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import space.crowdforce.AbstractIT
import space.crowdforce.controllers.model.UserUI
import space.crowdforce.domain.UserIdentity
import space.crowdforce.dsl.GiveMe
import javax.inject.Inject

class AuthAnonymousControllerIT : AbstractIT() {
    @Inject
    private lateinit var webTestClient: WebTestClient

    @Inject
    private lateinit var giveMe: GiveMe

    @AfterEach
    internal fun cleanUp() = giveMe.emptyDatabase()

    @Test
    fun `Should authenticate new user and return authenticated user`() {
        val username = "test_user_name"
        webTestClient.get()
            .uri("/api/v1/auth/anonymous?username=$username")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody<UserUI>().isEqualTo(UserUI(username))
    }

    @Test
    fun `Should authenticate existing user and return authenticated user`() {
        val anonymousId = "anonId123456"
        val user = giveMe.user(UserIdentity.ANON.identityKey(anonymousId)).please()

        webTestClient.get()
            .uri("/api/v1/auth/anonymous?anonymousId=$anonymousId&username=should_be_ignored")
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody<UserUI>().isEqualTo(UserUI(user.name))
    }
}