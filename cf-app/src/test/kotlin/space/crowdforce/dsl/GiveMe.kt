package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import space.crowdforce.model.Tables.PROJECTS
import space.crowdforce.model.Tables.USERS
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.user.UserService

@Component
class GiveMe(
    private var userService: UserService,
    private var projectService: ProjectService,
    private var objectMapper: ObjectMapper,
    private val dslContext: DSLContext
) {
    fun emptyDatabase() {
        dslContext.delete(USERS).execute()
        dslContext.delete(PROJECTS).execute()
    }
    fun user(userName: String): UserBuilder = UserBuilder(userName, userService, objectMapper)
    fun unauthorized() = GiveMeContext(null, userService, projectService, objectMapper)
    fun authorized(authorizedUserName: String) = GiveMeContext(authorizedUserName, userService, projectService, objectMapper)
}

class GiveMeContext(
    private var authorizedUserName: String?,
    private var userService: UserService,
    private var projectService: ProjectService,
    private var objectMapper: ObjectMapper
) {
    fun project(ownerName: String): ProjectBuilder = ProjectBuilder(authorizedUserName, ownerName, userService, projectService, objectMapper)
}