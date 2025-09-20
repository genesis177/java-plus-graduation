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
    private String eventDate;

    @NotNull(message = "Field: location. Error: must not be blank. Value: null")
    private LocationDto location;

    @NotNull(message = "Field: paid. Error: must not be blank. Value: null")
    private Boolean paid;

    @NotNull(message = "Field: participantLimit. Error: must not be blank. Value: null")
    @PositiveOrZero
    private Integer participantLimit;

    @NotNull(message = "Field: requestModeration. Error: must not be blank. Value: null")
    private Boolean requestModeration;

    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    @Size(min = 3, max = 120)
    private String title;
}
