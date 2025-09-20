package ru.practicum.request;

import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.request.dto.RequestGetDto;

import java.util.Collection;

public interface RequestService {

    Collection<RequestGetDto> getRequestsByUserId(long userId) throws NotFoundException;

    RequestGetDto createRequest(long userId, long eventId) throws NotFoundException, ConflictException;

    RequestGetDto cancelRequest(long userId, long requestId) throws NotFoundException, ConflictException;
}
