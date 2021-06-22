package space.crowdforce.domain.item

import java.time.LocalDateTime

data class TrackableItemEventParticipant(
    val id: Int,
    val trackableItemEventId: Int,
    val userId: Int,
    val creationTime: LocalDateTime,
    val lastUpdateTime: LocalDateTime,
    val confirmed: ConfirmationStatus
)

enum class ConfirmationStatus(
    val code: Int
) {
    WAIT_APPROVE(0),
    APPROVE_REJECTED(1),
    WAIT_COMPLETING(2),
    COMPLETING_REJECTED(3),
    COMPLETED(4);

    companion object {
        fun value(code: Int): ConfirmationStatus {
            for (value in values()) {
                if (value.code == code)
                    return value
            }

            throw IllegalArgumentException("Invalid code: $code")
        }
    }
}