package ru.practicum.exception.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponseDto {

    private String status;
    private String reason;
    private String message;
    private String timestamp;
}
