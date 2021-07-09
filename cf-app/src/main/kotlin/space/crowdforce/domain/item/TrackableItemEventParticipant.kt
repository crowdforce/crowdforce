package space.crowdforce.domain.item

import java.time.LocalDateTime

data class TrackableItemEventParticipant(
    val id: Int,
    val trackableItemEventId: Int,
    val userId: Int,
    val creationTime: LocalDateTime,
    val lastUpdateTime: LocalDateTime,
    val confirmed: ConfirmationStatus,
    val tgMessageId: Int
)

enum class ConfirmationStatus(
    val code: Int
) {
    WAIT_APPROVE(10),
    APPROVE_REJECTED(11),
    APPROVE_AUTO_REJECTED(12),
    WAIT_COMPLETING(20),
    COMPLETING_REJECTED(21),
    COMPLETED(22);

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