package ru.practicum.event.services.interfaces;

import java.util.Collection;

import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.dto.EventCreateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventUpdateDto;

public interface PrivateEventService {

    /**
     * Метод возвращает коллекцию {@link EventShortDto} по идентификатору пользователя
     *
     * @param userId идентификатор пользователя
     * @param from   номер начального элемента
     * @param size   максимальный размер коллекции
     */
    Collection<EventShortDto> getEventsByUserId(long userId, int from, int size) throws NotFoundException;

    /**
     * Метод получает несохранённый экземпляр класса {@link EventCreateDto}, проверяет его, передает для сохранения и
     * возвращает экземпляр класса {@link EventShortDto} после сохранения
     *
     * @param userId id текущего пользователя
     * @param dto    данные добавляемого события
     * @return данные добавленного события
     */
    EventFullDto createEvent(long userId, EventCreateDto dto) throws NotFoundException, ConflictException;

    /**
     * Метод возвращает экземпляр {@link EventFullDto} по переданному идентификатору пользователя и переданному
     * идентификатору события
     *
     * @param userId  id текущего пользователя
     * @param eventId id события
     * @return данные найденного события
     */
    EventFullDto getEventByUserIdAndEventId(long userId, long eventId) throws NotFoundException, ConflictException;

    /**
     * Метод проверяет и передаёт для сохранения экземпляр класса {@link EventUpdateDto}
     *
     * @param userId  id текущего пользователя
     * @param eventId id обновляемого события
     * @param dto     данные обновляемого события
     * @return данные обновленного события
     */
    EventFullDto updateEvent(long userId, long eventId, EventUpdateDto dto) throws NotFoundException, ConflictException;
}
