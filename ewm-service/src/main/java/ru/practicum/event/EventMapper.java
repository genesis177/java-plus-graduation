package ru.practicum.event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.EventCreateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.enums.States;
import ru.practicum.user.UserMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EventShortDto mapToEventShortDto(Event event) {
        log.info("Преобразование модели {} в модель {}", Event.class, EventShortDto.class);
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static Event mapToEvent(EventCreateDto dto) {
        log.info("Преобразование модели {} в модель {} для сохранения", EventCreateDto.class, Event.class);
        return Event.builder()
                .annotation(dto.getAnnotation())
                .description(dto.getDescription())
                .eventDate(LocalDateTime.parse(dto.getEventDate(), DATE_TIME_FORMATTER))
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .createdOn(LocalDateTime.now())
                .state(States.PENDING)
                .build();
    }

    public static EventFullDto mapToFullDto(Event event) {
        log.info("Преобразование модели {} в полную модель {} для сохранения", Event.class, EventFullDto.class);
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                .initiator(UserMapper.mapToUserDto(event.getInitiator()))
                .location(LocationMapper.mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().format(DATE_TIME_FORMATTER))
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public static void updateFields(Event event, EventUpdateDto dto) {
        log.info("Дополнение модели {} данными из модели {}", Event.class, EventUpdateDto.class);

        if (dto.hasAnnotation()) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.hasDescription()) {
            event.setDescription(dto.getDescription());
        }

        if (dto.hasEventDate()) {
            event.setEventDate(LocalDateTime.parse(dto.getEventDate(), DATE_TIME_FORMATTER));
        }

        if (dto.hasLocation()) {
            event.setLocation(LocationMapper.mapToLocation(dto.getLocation()));
        }

        if (dto.hasPaid()) {
            event.setPaid(dto.getPaid());
        }

        if (dto.hasParticipantLimit()) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.hasRequestModeration()) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.hasStateAction()) {
            switch (dto.getStateAction()) {
                case CANCEL_REVIEW -> event.setState(States.CANCELED);
                case SEND_TO_REVIEW -> event.setState(States.PENDING);
                default -> throw new IllegalStateException("Unexpected value: " + dto.getStateAction());
            }
        }

        if (dto.hasTitle()) {
            event.setTitle(dto.getTitle());
        }
    }
}
