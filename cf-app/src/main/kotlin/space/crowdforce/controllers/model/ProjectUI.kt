package space.crowdforce.controllers.model

data class ProjectUI(
    val id: Int,
    val name: String,
    val description: String,
    val lng: Double,
    val lat: Double,
    val subscribed: Boolean,
    val privilege: Privilege
)
