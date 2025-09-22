package ru.practicum.event.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryService;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventAdminUpdateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.enums.StateActionsAdmin;
import ru.practicum.event.enums.States;
import ru.practicum.event.services.interfaces.AdminEventService;
import ru.practicum.location.Location;
import ru.practicum.location.LocationMapper;
import ru.practicum.location.LocationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public EventFullDto updateEvent(EventAdminUpdateDto eventAdminUpdateDto, long eventId)
            throws NotFoundException, BadRequestException, ConflictException {
        log.info("Получен запрос на обновление события с id: {}", eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id: " + eventId + " не найдено"));

        if (eventAdminUpdateDto.getEventDate() != null && event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Изменяемая дата не может быть в прошлом");
        }

        LocalDateTime now = LocalDateTime.now();

        log.info("Проверяем состояние state: {}", event.getState());
        processState(event, eventAdminUpdateDto.getStateAction(), now);
        log.info("Обновляем поля");
        updateEventFields(event, eventAdminUpdateDto);

        log.info("Событие обновлено, state: {}", event.getState());

        return EventMapper.mapToFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsForAdmin(List<Long> users,
                                                List<String> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Integer from,
                                                Integer size) throws BadRequestException {
        log.info("Получен запрос администратора на получение события с фильтрами");
        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new BadRequestException("Время начала не может быть позже времени конца");
        }

        List<Event> events = eventRepository.findAllByFiltersAdmin(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from, size));

        return events.stream()
                .map(EventMapper::mapToFullDto)
                .collect(Collectors.toList());
    }


    private void processState(Event event, StateActionsAdmin state, LocalDateTime now)
            throws ConflictException, BadRequestException {

        if (state == null) return;

        switch (state) {
            case PUBLISH_EVENT -> {
                if (event.getState() != States.PENDING) {
                    throw new ConflictException("Только события в статусе ожидания могут быть опубликованы");
                }
                if (event.getEventDate().isBefore(now)) {
                    throw new BadRequestException("Дата события не может быть в прошлом");
                }
                if (event.getEventDate().isBefore(now.plusHours(1))) {
                    throw new ConflictException("Время старта события должно быть позже, минимум +1 час");
                }
                event.setState(States.PUBLISHED);
                event.setPublishedOn(now);
            }
            case REJECT_EVENT -> {
                if (event.getState() == States.PUBLISHED) {
                    throw new ConflictException("Только неопубликованные события могут быть отменены");
                }
                event.setState(States.CANCELED);
            }
        }
    }

    private void updateEventFields(Event event, EventAdminUpdateDto updateRequest)
            throws NotFoundException {
        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryService.findById(updateRequest.getCategory());
            event.setCategory(category);
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getLocation() != null) {
            Location newLocation = LocationMapper.mapToLocation(updateRequest.getLocation());
            locationService.save(newLocation);
            event.setLocation(newLocation);
        }
    }
}
