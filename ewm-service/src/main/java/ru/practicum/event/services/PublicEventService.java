package ru.practicum.event.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.States;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicEventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsWithFilters(String text, List<Long> categories, Boolean paid,
                                                    LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                    Boolean onlyAvailable, String sort, Integer from,
                                                    Integer size, HttpServletRequest request) throws BadRequestException {
        log.info("Получен запрос от публичного юзера на получение событий с фильтрами");
        if ((rangeStart != null) && (rangeEnd != null) && (rangeStart.isAfter(rangeEnd))) {
            throw new BadRequestException("Время начала не может быть позже окончания");
        }

        List<Event> events = eventRepository.findAllByFiltersPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, PageRequest.of(from, size));

        try {
            statsClient.postHit(HitDto.builder()
                    .app("main-service")
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.info("Не удалось отправить запрос о сохранении статистики " + e.getMessage());
        }

        Map<Long, Long> views = getAmountOfViews(events);

        return events.stream()
                .map(event -> {
                    EventShortDto dto = EventMapper.mapToEventShortDto(event);
                    dto.setViews(views.getOrDefault(event.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public EventFullDto getEventById(Long eventId, HttpServletRequest request) throws NotFoundException, BadRequestException {
        log.info("Получен запрос от публичного юзера на получение полной информации о событии с id {}", eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id " + eventId + " не найдено"));

        if (event.getState() != States.PUBLISHED) {
            throw new NotFoundException("Событие с id " + eventId + " недоступно, так как не опубликовано");
        }

        try {
            statsClient.postHit(HitDto.builder()
                    .app("main-service")
                    .uri(request.getRequestURI().toString())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.info("Не удалось отправить запрос о сохранении на сервер статистики" + e.getMessage());
        }
        EventFullDto eventFullDto = EventMapper.mapToFullDto(event);
        Map<Long, Long> views = getAmountOfViews(List.of(event));
        eventFullDto.setViews(views.getOrDefault(event.getId(), 0L));

        return eventFullDto;
    }

    private Map<Long, Long> getAmountOfViews(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .distinct()
                .collect(Collectors.toList());

        LocalDateTime startTime = events.stream()
                .map(Event::getCreatedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusYears(1));
        LocalDateTime endTime = LocalDateTime.now();

        Map<Long, Long> viewsMap = new HashMap<>();
        try {
            log.info("Получение статистики по времени для URI: {} c {} по {}", uris, startTime, endTime);

            ResponseEntity<List<StatsDto>> responseEntity = statsClient.getStats(startTime, endTime, uris, true);
            if (responseEntity == null || !responseEntity.getStatusCode().is2xxSuccessful()
                    || responseEntity.getBody() == null) {
                log.info("Сервис статистики вернул пустой или не 2хх ответ");
                return Collections.emptyMap();
            }

            List<StatsDto> stats = responseEntity.getBody();

            for (StatsDto s : stats) {
                String uri = s.getUri();
                Long hits = s.getHits() != null ? s.getHits() : 0L;
                Long eventId = Long.parseLong(uri.substring("/events/".length()));

                viewsMap.put(eventId, hits);
            }
        } catch (Exception e) {
            log.debug("Ошибка при получении статистики просмотров");
        }
        return viewsMap;
    }
}
