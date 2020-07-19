package space.crowdforce.domain

import java.time.LocalDateTime

data class Goal(
    val id: Int,
    val name: String,
    val description: String,
    val creationTime: LocalDateTime,
    val progressBar: Int,
    val projectId: Int
)
