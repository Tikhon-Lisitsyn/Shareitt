package ru.practicum.error;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.EmailAlreadyExistsException;
import ru.practicum.exception.InvalidBookingException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

@AllArgsConstructor
@RestControllerAdvice
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Ошибка: {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка с входным параметром", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Ошибка: {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка с входным параметром", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleException(final Throwable e) {
        log.error("Ошибка: {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка с входным параметром", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleSameEmailException(final EmailAlreadyExistsException e) {
        log.error("Ошибка: {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка с входным параметром", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleInvalidBookingException(final InvalidBookingException e) {
        log.error("Ошибка: {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка с входным параметром", e.getMessage());
    }
}