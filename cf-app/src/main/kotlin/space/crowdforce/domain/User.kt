package space.crowdforce.domain

data class User(
    val id: Int,
    val telegramId: Int,
    val name: String
) {
    companion object {
        val NULL_USER = User(-1, -1, "")
    }
}
