package space.crowdforce.controllers.model

import java.time.LocalDateTime

data class ActivityUI(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val participate: Boolean
)
