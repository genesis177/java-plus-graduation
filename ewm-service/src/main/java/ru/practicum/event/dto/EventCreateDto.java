package ru.practicum.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.location.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventCreateDto {

    @NotBlank(message = "Field: annotation. Error: must not be blank. Value: null")
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull(message = "Field: category. Error: must not be blank. Value: null")
    @Positive
    private Long category;

    @NotBlank(message = "Field: description. Error: must not be blank. Value: null")
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull(message = "Field: eventDate. Error: must not be blank. Value: null")
    @Future
    private LocalDateTime eventDate;

    @NotNull(message = "Field: location. Error: must not be blank. Value: null")
    private LocationDto location;

    @Builder.Default
    private Boolean paid = false;

    @PositiveOrZero
    @Builder.Default
    private Integer participantLimit = 0;

    @Builder.Default
    private Boolean requestModeration = true;

    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    @Size(min = 3, max = 120)
    private String title;
}
