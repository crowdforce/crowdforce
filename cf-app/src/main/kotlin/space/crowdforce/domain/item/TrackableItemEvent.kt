package space.crowdforce.domain.item

import java.time.LocalDateTime

data class TrackableItemEvent(
    val id: Int,
    val message: String,
    val trackableItemId: Int,
    val eventTime: LocalDateTime,
    val participantsNumber: Int
)