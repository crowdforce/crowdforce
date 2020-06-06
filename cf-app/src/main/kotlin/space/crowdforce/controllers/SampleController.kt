package space.crowdforce.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.crowdforce.controllers.dto.toDto
import space.crowdforce.entities.SampleEntity
import space.crowdforce.services.SampleService

@Api(value = "/", description = "Sample API")
@RestController
@RequestMapping("/api/v1")
class SampleController(
    private val sampleService: SampleService
) {
    @GetMapping("/sample/{id}", produces = [APPLICATION_JSON_VALUE])
    @ApiOperation(value = "Find sample entity by ID", nickname = "findSampleEntityById", response = SampleEntity::class)
    @ApiResponses(value = [
        ApiResponse(code = 400, message = "Invalid ID supplied"),
        ApiResponse(code = 404, message = "Entity not found")
    ])
    fun findSampleEntityById(
        @PathVariable("id")
        @ApiParam(value = "ID of entity", required = true)
        id: Long
    ) = sampleService.findSampleEntity(id).toDto()
}
