package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import space.crowdforce.service.user.UserService
import java.util.*

class UserBuilder(
    private val telegramId: Int,
    private var userService: UserService,
    objectMapper: ObjectMapper
) : AbstractBuilder<Int>(objectMapper) {
    override fun please(): Int {
        return userService.getOrCreateUser(telegramId, UUID.randomUUID().toString()).id
    }
}