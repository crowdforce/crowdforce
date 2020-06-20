package space.crowdforce.controllers.model

import java.time.LocalDateTime

data class ActivityUI(
        val id: Int,
        val name: String,
        val lat: Float,
        val lng: Float,
        val startDate: LocalDateTime,
        val endDate: LocalDateTime,
        val isSubscribed: Boolean
);
