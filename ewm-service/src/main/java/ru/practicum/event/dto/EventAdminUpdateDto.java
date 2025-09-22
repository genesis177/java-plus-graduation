package ru.practicum.event.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.enums.StateActionsAdmin;
import ru.practicum.location.LocationDto;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
public class EventAdminUpdateDto {


    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "Лимит участников не может быть отрицательным или равен нулю")
    private Integer participantLimit;

    private Boolean requestModeration;

    StateActionsAdmin stateAction;

    @Size(min = 3, max = 120)
    String title;
}
