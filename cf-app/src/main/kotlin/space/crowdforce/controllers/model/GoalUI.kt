package space.crowdforce.controllers.model

import java.time.LocalDateTime

data class GoalUI(
    val id: Int,
    val name: String,
    val description: String,
    val progress: Int,
    val creationTime: LocalDateTime
)
