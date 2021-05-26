package space.crowdforce.controllers.model

data class TrackableItemUI(
    val id: Int,
    val activityId: Int,
    val name: String,
    val status: ItemStatus = ItemStatus.GREEN
)

enum class ItemStatus {
    RED, GREEN, YELLOW
}
