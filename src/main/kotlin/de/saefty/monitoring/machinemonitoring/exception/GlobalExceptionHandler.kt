package de.saefty.monitoring.machinemonitoring.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(MachineNotFoundException::class)
    fun handleDkbException(ex: MachineNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val status = HttpStatus.BAD_REQUEST

        return ResponseEntity.status(status)
            .body(BaseHttpException(status.value(), ex.message))
    }
}
