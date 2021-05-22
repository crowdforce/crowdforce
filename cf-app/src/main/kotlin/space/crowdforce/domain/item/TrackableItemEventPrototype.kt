package space.crowdforce.domain.item

import java.time.LocalDateTime

data class TrackableItemEventPrototype(
    val message: String,
    val startDate: LocalDateTime,
    val recurring: Period
)