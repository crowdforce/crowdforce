package space.crowdforce.controllers.model

import java.time.LocalDateTime

data class ActivityFormUI(
    val name: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)
