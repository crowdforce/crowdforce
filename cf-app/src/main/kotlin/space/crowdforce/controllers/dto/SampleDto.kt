package space.crowdforce.controllers.dto

import space.crowdforce.entities.SampleEntity

data class SampleDto(
    val id: Long,
    val name: String
)

fun SampleDto?.toEntity() = this?.let {
    SampleEntity(
        id = it.id,
        name = it.name
    )
}

fun SampleEntity?.toDto() = this?.let {
    SampleDto(
        id = it.id,
        name = it.name
    )
}
