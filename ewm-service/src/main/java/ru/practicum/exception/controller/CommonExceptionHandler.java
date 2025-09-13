package ru.practicum.exception.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.dto.ErrorResponseDto;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(final ConflictException e) {
        log.warn("Вызвано исключение ConflictException с текстом {}", e.getMessage());

        return new ResponseEntity<>(ErrorResponseDto.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("For the requested operation the conditions are not met.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(final NotFoundException e) {
        log.warn("Вызвано исключение NotFoundException с текстом {}", e.getMessage());

        return new ResponseEntity<>(ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build(),
                HttpStatus.NOT_FOUND);
    }
}
