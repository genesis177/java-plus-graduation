package ru.practicum.common.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleConflict(final ConflictException e) {
        log.warn("Вызвано исключение ConflictException с текстом {}", e.getMessage());

        return ErrorResponseDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(final NotFoundException e) {
        log.warn("Вызвано исключение NotFoundException с текстом {}", e.getMessage());

        return ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleAlreadyExists(final AlreadyExistsException e) {
        log.warn("Вызвано исключение AlreadyExistsException с текстом {}", e.getMessage());

        return ErrorResponseDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("The required object already exists.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build();
    }
}