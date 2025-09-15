package ru.practicum.event.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    @NotNull(message = "Field: lat. Error: must not be blank. Value: null")
    @PositiveOrZero
    private Float lat;

    @NotNull(message = "Field: lon. Error: must not be blank. Value: null")
    @PositiveOrZero
    private Float lon;
}
