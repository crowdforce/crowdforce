package space.crowdforce.controllers

import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import space.crowdforce.AbstractIT
import javax.inject.Inject

class AuthControllerIT : AbstractIT() {
    @Inject
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `Should return list of projects with auth user`() {
        /** /login */

        /*       val id = 1
               val firstName = "Bob"
               val userName = "Just_bob"
               val photoUrl = "photo_url"
               val authDate = 1596991115
               val hash= "hash"

               val exchange = webTestClient.post()
                   .uri("/login?id=206798728&first_name=Maxim&last_name=Stepachev&username=ingvard&photo_url=https%3A%2F%2Ft.me%2Fi%2Fuserpic%2F320%2F2Hn1XYAz1r9QMFgdEpHOsV-t2AHYOCTZyk1SfsBTzB4.jpg&auth_date=1596991115&hash=98e34d04cb65dbf67d41d0167f03e3c8e21460ac67313e89cb8cdf1ebba536e9")
                   .exchange()

               exchange
                   .expectBody()
                   .consumeWith({

                       val zz = it.status
                   })*/

        /*   webTestClient.post()
               .uri("/api/v1/projects/${project.id}/goals")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON)
               .bodyValue(updatedGoal)
               .exchange()
               .expectStatus().is2xxSuccessful
               .expectBody()
               .jsonPath("$.id").isNotEmpty
               .jsonPath("$.name").isEqualTo(updatedGoal.name)
               .jsonPath("$.description").isEqualTo(updatedGoal.description)
               .jsonPath("$.progress").isEqualTo(updatedGoal.progress)*/
    }
}
