package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.crowdforce.controllers.model.ActivityUI
import space.crowdforce.controllers.model.ProjectUI
import java.time.LocalDateTime.now

@Api(value = "/api/v1/projects", description = "")
@RestController
@RequestMapping("/api/v1/projects")
class ProjectController {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ApiOperation(value = "")
    suspend fun getProjects() = listOf(
        ProjectUI(1, "test 1", 59.984945f, 30.343947f, false, emptyList()),
        ProjectUI(2, "test 2", 59.983000f, 30.350230f, true, listOf(
            ActivityUI(1232, "Activity 1", 59.981611f, 30.356375f, now(), now().plusDays(5), true)
        ))
    )
}