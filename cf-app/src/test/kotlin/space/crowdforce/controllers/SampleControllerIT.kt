package space.crowdforce.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import space.crowdforce.AbstractIT
import space.crowdforce.controllers.dto.toDto
import space.crowdforce.services.SampleService
import javax.inject.Inject

internal class SampleControllerIT : AbstractIT() {
    @Inject
    private lateinit var mockMvc: MockMvc

    @Inject
    private lateinit var sampleService: SampleService

    @Inject
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `Test find entity by id`() {
        // given:
        val entity = sampleService.createSampleEntity("test")

        // then:
        mockMvc
            .get("/api/v1/sample/${entity.id}")
            .andExpect {
                status { isOk }
                content { contentType(MediaType.APPLICATION_JSON) }
                content {
                    json(
                        entity.toDto().toJson()
                    )
                }
            }
    }

    private fun Any?.toJson(): String = objectMapper.writeValueAsString(this)
}
