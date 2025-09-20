package ru.practicum.event.services.interfaces;

import org.apache.coyote.BadRequestException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.dto.EventAdminUpdateDto;
import ru.practicum.event.dto.EventFullDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    EventFullDto updateEvent(EventAdminUpdateDto eventAdminUpdateDto, long eventId)
            throws NotFoundException, BadRequestException, ConflictException;


    List<EventFullDto> getEventsForAdmin(List<Long> users,
                                         List<String> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Integer from,
                                         Integer size) throws BadRequestException;
}
