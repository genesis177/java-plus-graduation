package ru.practicum.location;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LocationMapper {


    public LocationDto mapToLocationDto(Location location) {
        log.info("Преобразование модели БД {} в модель {}", Location.class, LocationDto.class);
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public Location mapToLocation(LocationDto location) {
        log.info("Преобразование модели {} в модель БД {}", LocationDto.class, Location.class);
        return Location.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
