package space.crowdforce.controllers.dto

data class AuthenticationNumber(
    val phoneNumber: String
)

data class AuthenticationCode(
    val code: String
)

data class TelegramUser(
    val userName: String
)
