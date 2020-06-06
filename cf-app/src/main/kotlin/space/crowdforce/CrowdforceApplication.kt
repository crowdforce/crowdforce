package space.crowdforce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.ZoneOffset.UTC
import java.util.TimeZone

@SpringBootApplication
class CrowdforceApplication

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone(UTC))
    runApplication<CrowdforceApplication>(*args)
}
