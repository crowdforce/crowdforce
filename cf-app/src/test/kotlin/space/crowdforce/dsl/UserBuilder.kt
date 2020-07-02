package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.runBlocking
import space.crowdforce.service.user.UserService

class UserBuilder(
    private val userName: String,
    private var userService: UserService,
    objectMapper: ObjectMapper
) : AbstractBuilder<Int>(objectMapper) {
    override fun please(): Int {
        runBlocking {
            userService.sendCodesToUser(userName)
        }

        return userService.getUserIdByName(userName)!!
    }
}