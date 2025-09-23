package ru.practicum.request;

import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.request.dto.RequestGetDto;
import ru.practicum.request.dto.RequestsChangeStatusRequestDto;
import ru.practicum.request.dto.RequestsChangeStatusResponseDto;

import java.util.List;

public interface RequestService {

    List<RequestGetDto> getRequestsByUserId(long userId)
            throws NotFoundException;

    RequestGetDto createRequest(long userId, long eventId)
            throws NotFoundException, ConflictException;

    RequestGetDto cancelRequest(long userId, long requestId)
            throws NotFoundException, ConflictException;

    List<RequestGetDto> getRequestsByEventId(Long userId, Long eventId)
            throws ConflictException, NotFoundException;

    RequestsChangeStatusResponseDto RequestsChangeStatusRequestDto(Long userId, Long eventId, RequestsChangeStatusRequestDto dto)
            throws ConflictException, NotFoundException;
}
