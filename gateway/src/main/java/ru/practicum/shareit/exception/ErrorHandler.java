package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public UnsupportedStatusError unsupportedStatusError(final MethodArgumentTypeMismatchException exception) {
        String error = "Unknown " + exception.getName() + ": " + exception.getValue();
        log.warn(error);
        return new UnsupportedStatusError(error);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationException validationError(final MethodArgumentNotValidException exception) {
        String error = "Недопустимое значение";
        log.warn(error);
        return new ValidationException(error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public NotFoundException serverError(final Throwable exception) {
        String error = exception.getMessage();
        log.warn(error);
        return new NotFoundException(error);
    }
}
