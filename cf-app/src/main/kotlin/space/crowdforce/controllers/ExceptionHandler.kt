package space.crowdforce.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import space.crowdforce.exceptions.InvalidVerificationException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [InvalidVerificationException::class])
    fun exception(ex: InvalidVerificationException): ResponseEntity<String> =
        ResponseEntity.badRequest().body(ex.message)
}
