package ru.practicum.location;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class LocationMapper {


    public static LocationDto mapToLocationDto(Location location) {
        log.info("Преобразование модели БД {} в модель {}", Location.class, LocationDto.class);
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location mapToLocation(LocationDto location) {
        log.info("Преобразование модели {} в модель БД {}", LocationDto.class, Location.class);
        return Location.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
