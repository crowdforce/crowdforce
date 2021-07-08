package space.crowdforce.domain.item

import java.time.LocalDateTime

class TrackableItemEventPrototypeLast(
    val id: Int,
    val message: String,
    val startDate: LocalDateTime,
    val recurring: Period,
    val lastEventDate: LocalDateTime?,
    val trackableItemId: Int
)