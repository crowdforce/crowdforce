package space.crowdforce.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import space.crowdforce.exception.OperationException
import space.crowdforce.exception.UnauthorizedAccessException

@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: Exception) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)

    @ExceptionHandler(UnauthorizedAccessException::class)
    fun handleSecurityException(e: Exception) =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)

    @ExceptionHandler(OperationException::class)
    fun handleOperationException(e: Exception) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
}
