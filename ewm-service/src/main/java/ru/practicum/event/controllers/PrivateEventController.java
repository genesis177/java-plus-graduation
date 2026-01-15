package ru.practicum.event.controllers;

import java.util.Collection;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.services.interfaces.PrivateEventService;
import ru.practicum.event.dto.EventCreateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.RequestGetDto;
import ru.practicum.request.dto.RequestsChangeStatusRequestDto;
import ru.practicum.request.dto.RequestsChangeStatusResponseDto;

/**
 * Закрытый API для работы с событиями
 */
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final PrivateEventService privateEventService;
    private final RequestService requestService;

    /**
     * Получение событий, добавленных текущим пользователем
     *
     * @param userId идентификатор пользователя
     * @param from   номер начального элемента
     * @param size   максимальный размер коллекции
     * @return коллекция {@link EventShortDto}
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getEventsByUserId(@PathVariable(name = "userId") long userId,
                                                       @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                       @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                       HttpServletResponse response) throws
            NotFoundException {

        Page<EventShortDto> page = privateEventService.getEventsByUserId(userId, from, size);
        response.setHeader("X-Total-Count", String.valueOf(page.getTotalElements()));

        return page.getContent();
    }


    /**
     * Добавление нового события
     *
     * @param userId id текущего пользователя
     * @param dto    данные добавляемого события
     * @return данные добавленного события
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(name = "userId") long userId,
                                    @RequestBody @Valid EventCreateDto dto) throws NotFoundException, ConflictException {
        return privateEventService.createEvent(userId, dto);
    }

    /**
     * Получение полной информации о событии, добавленном текущим пользователем
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return данные найденного события
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByUserIdAndEventId(@PathVariable(name = "userId") long userId,
                                                   @PathVariable(name = "eventId") long eventId) throws
            ConflictException,
            NotFoundException {
        return privateEventService.getEventByUserIdAndEventId(userId, eventId);
    }

    /**
     * Изменение события, добавленного текущим пользователем
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @param dto     новые данные события
     * @return данные обновленного события
     */
    @PatchMapping(value = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable(name = "userId") long userId,
                                    @PathVariable(name = "eventId") long eventId,
                                    @RequestBody @Valid EventUpdateDto dto) throws ConflictException, NotFoundException {
        return privateEventService.updateEvent(userId, eventId, dto);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return информация о запросах
     */
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestGetDto> getRequestsByUserIdAndEventId(
            @PathVariable(name = "userId") long userId,
            @PathVariable(name = "eventId") long eventId
    ) throws ConflictException, NotFoundException {
        return requestService.getRequestsByEventId(userId, eventId);
    }

    /**
     * Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @param dto     данные добавляемого события
     * @return измененные заявки
     */
    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestsChangeStatusResponseDto changeRequestsStatus(
            @PathVariable(name = "userId") long userId,
            @PathVariable(name = "eventId") long eventId,
            @RequestBody @Valid RequestsChangeStatusRequestDto dto
    ) throws ConflictException, NotFoundException {
        return requestService.requestsChangeStatus(userId, eventId, dto);
    }
}
