package ru.practicum.event.service;

import ru.practicum.dto.event.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    // Публичные методы
    Collection<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Boolean onlyAvailable, String sort, Integer from, Integer size);

    EventFullDto getEvent(Long userId, Long eventId);

    // Приватные методы
    Collection<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    // Админские методы
    Collection<EventFullDto> getEventsAdmin(List<Long> users, List<String> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    //Для реквестов
    void updateEventForRequests(Long eventId, EventFullDto eventFullDto);

    EventFullDto getEventByIdFeign(Long eventId);

    EventFullDto getEventByUserFeign(Long eventId, Long userId);

    List<EventFullDto> getRecommendations(Long userId);

    void likeEvent(Long userId, Long eventId);
}