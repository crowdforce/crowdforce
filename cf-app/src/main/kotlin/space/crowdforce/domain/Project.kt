package space.crowdforce.domain

import space.crowdforce.domain.geo.Location
import java.time.LocalDateTime

data class Project(
    val id: Int,
    val name: String,
    val description: String,
    val ownerId: Int,
    val creationTime: LocalDateTime,
    val location: Location,
    val subscribed: Boolean
)
