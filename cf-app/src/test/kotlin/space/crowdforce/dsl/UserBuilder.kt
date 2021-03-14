package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import space.crowdforce.domain.User
import space.crowdforce.domain.UserIdentityKey
import space.crowdforce.service.user.UserService
import java.util.UUID

class UserBuilder(
    private val userIdentityKey: UserIdentityKey,
    private var userService: UserService,
    objectMapper: ObjectMapper
) : AbstractBuilder<User>(objectMapper) {
    override fun please(): User {
        return userService.getOrCreateUser(userIdentityKey, UUID.randomUUID().toString())
    }
}