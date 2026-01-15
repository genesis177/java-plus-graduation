package ru.practicum.request.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.request.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestsChangeStatusRequestDto {

    @NotEmpty(message = "Список requestIds не может быть пустым")
    List<Long> requestIds;

    @NotNull(message = "Статус не может быть null")
    RequestStatus status;
}
