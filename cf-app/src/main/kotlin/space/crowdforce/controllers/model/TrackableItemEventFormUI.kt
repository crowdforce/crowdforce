package space.crowdforce.controllers.model

import space.crowdforce.domain.item.Period
import java.time.LocalDateTime

data class TrackableItemEventFormUI(
    val message: String,
    val startDate: LocalDateTime,
    val recurring: Period
)