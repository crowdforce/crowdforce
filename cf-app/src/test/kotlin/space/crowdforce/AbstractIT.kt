package space.crowdforce

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.io.File

@ExtendWith(value = [SpringExtension::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class AbstractIT {
    companion object {
        const val TEST_USER = "test_user"

        private val dockerContainers = KDockerComposeContainer(File("src/test/resources/docker-compose-test.yml"))
            .withLocalCompose(true)
            .withExposedService("postgres", 5432, Wait.forListeningPort())

        init {
            dockerContainers.start()
            System.setProperty(
                "spring.datasource.url",
                "jdbc:postgresql://localhost:${dockerContainers.getServicePort(
                    "postgres",
                    5432
                )}/?loggerLevel=DEBUG"
            )
        }
    }
}

class KDockerComposeContainer(file: File) : DockerComposeContainer<KDockerComposeContainer>(file)
