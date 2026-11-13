package com.foodsave.platform.shared.interfaces.rest;

import java.net.URI;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messages;

    public GlobalExceptionHandler(MessageSource messages) {
        this.messages = messages;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException exception, Locale locale) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                messages.getMessage(exception.messageKey(), null, locale));
        detail.setType(URI.create("https://foodsave.app/problems/not-found"));
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleInvalid(IllegalArgumentException exception, Locale locale) {
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                messages.getMessage(exception.getMessage(), null, exception.getMessage(), locale));
        detail.setType(URI.create("https://foodsave.app/problems/invalid-request"));
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, Locale locale) {
        var fields = exception.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + messages.getMessage(error, locale))
                .toList();
        var detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                messages.getMessage("validation.failed", null, locale));
        detail.setProperty("errors", fields);
        detail.setType(URI.create("https://foodsave.app/problems/validation"));
        return detail;
    }
}
