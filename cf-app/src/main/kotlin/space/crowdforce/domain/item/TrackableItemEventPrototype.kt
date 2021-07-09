package space.crowdforce.domain.item

import java.time.LocalDateTime

data class TrackableItemEventPrototype(
    val id: Int,
    val message: String,
    val startDate: LocalDateTime,
    val recurring: Period,
    val trackableItemId: Int,
    val participantsNumber: Int
)