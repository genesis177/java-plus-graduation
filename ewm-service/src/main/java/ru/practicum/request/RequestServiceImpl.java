package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.enums.States;
import ru.practicum.request.dto.RequestGetDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public Collection<RequestGetDto> getRequestsByUserId(long userId) throws NotFoundException {
        log.info("Запрос списка заявок пользователя на участие в чужих событиях на уровне сервиса");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Collection<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public RequestGetDto createRequest(long userId, long eventId) throws NotFoundException, ConflictException {
        log.info("Запрос на добавление запроса от текущего пользователя на участие в событии на уровне сервиса");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getInitiator().getId().equals(userId))
            throw new ConflictException("User with id=" + userId + " is initiator of event with id=" + eventId);

        if (event.getState() != States.PUBLISHED)
            throw new ConflictException("Event not published with id=" + eventId);

        Integer eventParticipantCount = requestRepository.getParticipantCount(eventId);

        if (event.getParticipantLimit().equals(eventParticipantCount))
            throw new ConflictException("Event reached with id=" + eventId);

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(RequestStatus.PENDING)
                .build();

        Request saveRequest = requestRepository.save(request);
        return RequestMapper.toRequestGetDto(saveRequest);
    }

    @Override
    @Transactional
    public RequestGetDto cancelRequest(long userId, long requestId) throws NotFoundException, ConflictException {
        log.info("Запрос на отмену своего запроса на участие в событии");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException(
                    "User with id=" + userId + " is not requester of request with id=" + requestId);
        }

        request.setStatus(RequestStatus.CANCELED);
        Request saveRequest = requestRepository.save(request);
        return RequestMapper.toRequestGetDto(saveRequest);
    }

}
