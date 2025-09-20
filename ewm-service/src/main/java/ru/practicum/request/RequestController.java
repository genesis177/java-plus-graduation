package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.request.dto.RequestGetDto;

import java.util.Collection;

/**
 * Закрытый API для работы с запросами текущего пользователя на участие в событиях
 */
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RequestGetDto> getRequestsByUserId(
        @PathVariable(name = "userId") long userId
    ) throws NotFoundException {
        return requestService.getRequestsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestGetDto createRequest(
            @PathVariable(name = "userId") long userId,
            @RequestParam(name = "eventId") long eventId
    ) throws NotFoundException, ConflictException {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestGetDto cancelRequest(
            @PathVariable(name = "userId") long userId,
            @PathVariable(name = "requestId") long requestId
    ) throws NotFoundException, ConflictException {
        return requestService.cancelRequest(userId, requestId);
    }
}
