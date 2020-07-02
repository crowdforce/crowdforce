package space.crowdforce.domain

import java.time.LocalDateTime

data class Activity(
    val id: Int,
    val name: String,
    val description: String,
    val creationData: LocalDateTime,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val projectId: Int,
    val participant: Boolean
)
