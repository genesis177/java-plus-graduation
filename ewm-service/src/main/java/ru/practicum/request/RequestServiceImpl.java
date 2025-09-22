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
import ru.practicum.request.dto.RequestsChangeStatusRequestDto;
import ru.practicum.request.dto.RequestsChangeStatusResponseDto;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    public List<RequestGetDto> getRequestsByUserId(long userId)
            throws NotFoundException
    {
        log.info("Запрос списка заявок пользователя с id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        log.info("Количество найденных заявок: {}", requests.size());

        return requests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestGetDto createRequest(long userId, long eventId)
            throws NotFoundException, ConflictException
    {
        log.info("Добавление запроса от текущего пользователя id {} на участие в событии id {}", userId, eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        validateParamsForRequestCreate(user, event);

        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build();

        Request saveRequest = requestRepository.save(request);
        log.info("Создан запрос с id: {} в статусе {}", saveRequest.getId(), saveRequest.getStatus());

        return RequestMapper.toRequestGetDto(saveRequest);
    }

    @Override
    @Transactional
    public RequestGetDto cancelRequest(long userId, long requestId)
            throws NotFoundException, ConflictException
    {
        log.info("Запрос на отмену своего запроса на участие в событии");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("User with id " + userId + " is not requester of request with id " + requestId);
        }

        if (!request.getStatus().equals(RequestStatus.CANCELED)) {
            request.setStatus(RequestStatus.CANCELED);
            Request saveRequest = requestRepository.save(request);
            log.info("Запрос отменен");
            return RequestMapper.toRequestGetDto(saveRequest);
        } else {
            log.info("Запрос уже имеет статус {}", request.getStatus());
            return RequestMapper.toRequestGetDto(request);
        }
    }

    @Override
    public List<RequestGetDto> getRequestsByUserIdAndEventId(Long userId, Long eventId)
            throws ConflictException, NotFoundException
    {
        log.info("Получение запросов на участие в событии id {} пользователем id {}", eventId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(user.getId()))
            throw new ConflictException("User with id " + user.getId() + " is not initiator of event with id=" + event.getId());

        List<Request> requests = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        log.info("Количество найденных запросов: {}", requests.size());

        return requests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestsChangeStatusResponseDto RequestsChangeStatusRequestDto(Long userId, Long eventId, RequestsChangeStatusRequestDto dto)
            throws ConflictException, NotFoundException
    {
        log.info("Изменение статуса заявок на участие в событии id {} текущего пользователя id {}", eventId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(user.getId()))
            throw new ConflictException("User with id " + user.getId() + " is not initiator of event with id=" + event.getId());

        List<Long> requestIds = dto.getRequestIds();

        List<Request> requests = requestRepository.findByIdAndEventId(requestIds, eventId);

        if (requests.size() != requestIds.size()) {
            Set<Long> foundRequestIds = requests.stream()
                    .map(Request::getId)
                    .collect(Collectors.toSet());

            List<Long> missingOrWrongEventIds = requestIds.stream()
                    .filter(id -> !foundRequestIds.contains(id))
                    .toList();

            throw new NotFoundException("Requests with ids: " + missingOrWrongEventIds +
                    " were not found to event id: " + eventId);
        }

        boolean isExistNotPendingRequest = requests.stream()
                .anyMatch(x -> !x.getStatus().equals(RequestStatus.PENDING));
        if (isExistNotPendingRequest)
            throw new ConflictException("There is a request that is not in the pending status");

        int confirmedRequestsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int availableSlots = event.getParticipantLimit() - confirmedRequestsCount;

        List<Long> confirmedIds = Collections.emptyList();
        List<Long> rejectedIds = Collections.emptyList();

        RequestStatus newStatus = dto.getStatus();
        if (newStatus.equals(RequestStatus.CONFIRMED)) {

            if ((availableSlots - requests.size()) < 0)
                throw new ConflictException("The participant limit has been reached for event id: " + eventId);

            requestRepository.updateStatusByIds(requestIds, RequestStatus.CONFIRMED);
            confirmedIds = requestIds;

            if ((availableSlots - requests.size()) == 0) {
                List<Request> rejectedRequests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);

                rejectedIds = rejectedRequests.stream()
                        .map(Request::getId)
                        .toList();

                requestRepository.updateStatusByIds(rejectedIds, RequestStatus.CONFIRMED);

            }
        } else if (newStatus.equals(RequestStatus.REJECTED)) {
            requestRepository.updateStatusByIds(requestIds, RequestStatus.REJECTED);
            rejectedIds = requestIds;
        }

        List<Request> confirmedRequests = confirmedIds.isEmpty() ?
                Collections.emptyList() : requestRepository.findAllById(confirmedIds);

        List<Request> rejectedRequests = rejectedIds.isEmpty() ?
                Collections.emptyList() : requestRepository.findAllById(rejectedIds);

        RequestsChangeStatusResponseDto response = new RequestsChangeStatusResponseDto();
        response.setConfirmedRequests(confirmedRequests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList()));

        response.setRejectedRequests(rejectedRequests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList()));

        return response;
    }

    private void validateParamsForRequestCreate(User user, Event event)
            throws ConflictException
    {
        if (event.getInitiator().getId().equals(user.getId()))
            throw new ConflictException("User with id=" + user.getId() + " is initiator of event with id=" + event.getId());

        if (requestRepository.existsByRequesterIdAndEventId(user.getId(), event.getId()))
            throw new ConflictException("Request already exists for this user and event");

        if (!event.getState().equals(States.PUBLISHED))
            throw new ConflictException("Event not published with id=" + event.getId());

        Integer eventParticipantCount = requestRepository.countByEventId(event.getId());

        if (event.getParticipantLimit().equals(eventParticipantCount))
            throw new ConflictException("Event reached with id=" + event.getId());
    }
}
