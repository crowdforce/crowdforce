package space.crowdforce.controllers.model

data class ProjectUI(
        val id: Int,
        val name: String,
        val lat: Float,
        val lng: Float,
        val isSubscribed: Boolean,
        val activities: List<ActivityUI>
)
