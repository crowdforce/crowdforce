package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import space.crowdforce.domain.Goal
import space.crowdforce.model.Tables.PROJECTS
import space.crowdforce.model.Tables.USERS
import space.crowdforce.service.goal.GoalService
import space.crowdforce.service.mapper.MapperService
import space.crowdforce.service.project.ProjectService
import space.crowdforce.service.user.UserService

@Component
class GiveMe(
    private val userService: UserService,
    private val projectService: ProjectService,
    private val objectMapper: ObjectMapper,
    private val goalService: GoalService,
    private val dslContext: DSLContext,
    private val mapperService: MapperService
) {
    fun emptyDatabase() {
        dslContext.delete(USERS).execute()
        dslContext.delete(PROJECTS).execute()
    }

    fun user(telegramId: Int): UserBuilder = UserBuilder(telegramId, userService, objectMapper)
    fun unauthorized() = GiveMeContext(null, userService, projectService, goalService, objectMapper)
    fun authorized(authorizedUserTelegramId: Int) = GiveMeContext(authorizedUserTelegramId, userService, projectService, goalService, objectMapper)

    fun json(obj: Any): String {
        if (obj is List<*>)
            return objectMapper.writeValueAsString(obj.map {
                if (it is Goal)
                    mapperService.map(it)
            })

        if (obj is Goal)
            return objectMapper.writeValueAsString(mapperService.map(obj))

        throw RuntimeException("Unsupported mapper for $obj")
    }
}

class GiveMeContext(
    private var authorizedUserName: Int?,
    private var userService: UserService,
    private var projectService: ProjectService,
    private var goalService: GoalService,
    private var objectMapper: ObjectMapper
) {
    fun project(ownerTelegramId: Int): ProjectBuilder = ProjectBuilder(authorizedUserName, ownerTelegramId, userService, projectService, goalService, objectMapper)
}
