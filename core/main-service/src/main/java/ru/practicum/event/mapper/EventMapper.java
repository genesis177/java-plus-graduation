package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.Event;
import ru.practicum.event.Location;
import ru.practicum.event.dto.*;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "createdOn", source = "createdOn")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "publishedOn", ignore = true)
    Event toEvent(NewEventDto newEventDto, Category category, Location location, User initiator, LocalDateTime createdOn);

    @Mapping(target = "views", source = "views")
    EventFullDto toEventFullDto(Event event, Long views);

    @Mapping(target = "views", source = "views")
    EventShortDto toEventShortDto(Event event, Long views);

    @Mapping(target = "id", ignore = true)
    Location toLocation(LocationDto locationDto);

    LocationDto toLocationDto(Location location);

    UserShortDto toUserShortDto(User user);
}