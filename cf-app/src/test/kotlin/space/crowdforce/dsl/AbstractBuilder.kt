package space.crowdforce.dsl

import com.fasterxml.jackson.databind.ObjectMapper

abstract class AbstractBuilder<T>(private var objectMapper: ObjectMapper) {
    abstract fun please(): T
    fun pleaseJson(): String = objectMapper.writeValueAsString(please())
}