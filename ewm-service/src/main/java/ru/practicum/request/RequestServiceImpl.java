package ru.practicum.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<RequestGetDto> getRequestsByUserId(long userId, int from, int size)
            throws NotFoundException {
        log.info("Запрос списка заявок пользователя с id: {}", userId);

        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<Request> requests = requestRepository.findAllByRequesterId(user.getId(), pageRequest).getContent();
        log.info("Количество найденных заявок: {}", requests.size());

        return requests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestGetDto createRequest(long userId, long eventId)
            throws NotFoundException, ConflictException {
        log.info("Добавление запроса от текущего пользователя id {} на участие в событии id {}", userId, eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        validateForCreateRequest(user, event);

        Integer participantLimit = event.getParticipantLimit();
        boolean autoConfirmed = participantLimit == 0 || event.getRequestModeration().equals(false);

        int confirmedRequestsCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int availableSlots = participantLimit - confirmedRequestsCount;
        Request request;

        if (availableSlots > 0 || participantLimit == 0) {
            request = Request.builder()
                    .created(LocalDateTime.now())
                    .requester(user)
                    .event(event)
                    .status(autoConfirmed ? RequestStatus.CONFIRMED : RequestStatus.PENDING)
                    .build();
        } else {
            throw new ConflictException("Event reached with id=" + eventId);
        }

        Request saveRequest = requestRepository.save(request);
        log.info("Создан запрос с id: {} в статусе {}", saveRequest.getId(), saveRequest.getStatus());

        return RequestMapper.toRequestGetDto(saveRequest);
    }

    @Override
    @Transactional
    public RequestGetDto cancelRequest(long userId, long requestId)
            throws NotFoundException, ConflictException {
        log.info("Запрос на отмену своего запроса на участие в событии");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(user.getId())) {
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
    public List<RequestGetDto> getRequestsByEventId(Long userId, Long eventId)
            throws ConflictException, NotFoundException {
        log.info("Получение запросов на участие в событии id {} пользователем id {}", eventId, userId);

        Event event = baseValidateEvent(userId, eventId);

        List<Request> requests = requestRepository.findAllByEventId(event.getId());
        log.info("Количество найденных запросов: {}", requests.size());

        return requests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestsChangeStatusResponseDto requestsChangeStatus(Long userId, Long eventId,
                                                                RequestsChangeStatusRequestDto dto)
            throws ConflictException, NotFoundException {
        log.info("Изменение статуса заявок на участие в событии id {} текущего пользователя id {}", eventId, userId);

        Event event = baseValidateEvent(userId, eventId);

        List<Request> requests = validateAndGetRequests(dto.getRequestIds(), eventId);

        return switch (dto.getStatus()) {
            case CONFIRMED -> requestsChangeStatusToConfirmed(event, requests, dto);
            case REJECTED -> requestsChangeStatusToRejected(requests, dto);
            default -> throw new ConflictException("Unspecified status {} " + dto.getStatus());
        };
    }

    private Event baseValidateEvent(Long userId, Long eventId) throws NotFoundException, ConflictException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException("User with id " + userId + " is not initiator of event with id=" + eventId);
        }

        return event;
    }

    private List<Request> validateAndGetRequests(List<Long> requestIds, Long eventId) throws NotFoundException,
                                                                                             ConflictException {
        List<Request> requests = requestRepository.findByIdInAndEventId(requestIds, eventId);

        if (requests.size() != requestIds.size()) {
            Set<Long> foundRequestIds = requests.stream()
                    .map(Request::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = requestIds.stream()
                    .filter(id -> !foundRequestIds.contains(id))
                    .toList();

            throw new NotFoundException("Requests with ids: " + missingIds + " not found for event id: " + eventId);
        }

        boolean hasPendingStatus = requests.stream()
                .allMatch(x -> x.getStatus().equals(RequestStatus.PENDING));
        if (!hasPendingStatus) {
            throw new ConflictException("ids contains requests with not pending status");
        }

        return requests;
    }

    private void validateForCreateRequest(User user, Event event) throws ConflictException {

        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ConflictException(
                    "User with id=" + user.getId() + " is initiator of event with id=" + event.getId());
        }

        if (requestRepository.existsByRequesterIdAndEventId(user.getId(), event.getId())) {
            throw new ConflictException("Request already exists for this user and event");
        }

        if (!event.getState().equals(States.PUBLISHED)) {
            throw new ConflictException("Event not published with id=" + event.getId());
        }
    }

    private RequestsChangeStatusResponseDto requestsChangeStatusToConfirmed(Event event, List<Request> requests,
                                                                            RequestsChangeStatusRequestDto dto) throws
                                                                                                                ConflictException {

        validateLimit(event, dto);

        List<Request> confirmedRequests = requests
                .stream()
                .filter(request -> dto.getRequestIds().contains(request.getId()))
                .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                .toList();

        List<Request> rejectedRequests = requests
                .stream()
                .filter(request -> !dto.getRequestIds().contains(request.getId()))
                .peek(request -> request.setStatus(RequestStatus.REJECTED))
                .toList();

        requestRepository.flush();

        RequestsChangeStatusResponseDto response = new RequestsChangeStatusResponseDto();

        response.setConfirmedRequests(confirmedRequests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList()));

        response.setRejectedRequests(rejectedRequests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList()));

        return response;
    }

    private RequestsChangeStatusResponseDto requestsChangeStatusToRejected(List<Request> requests,
                                                                           RequestsChangeStatusRequestDto dto) {
        List<Request> rejectedRequests = requests
                .stream()
                .filter(request -> dto.getRequestIds().contains(request.getId()))
                .peek(request -> request.setStatus(RequestStatus.REJECTED))
                .toList();

        requestRepository.flush();

        RequestsChangeStatusResponseDto response = new RequestsChangeStatusResponseDto();

        response.setRejectedRequests(rejectedRequests.stream()
                .map(RequestMapper::toRequestGetDto)
                .collect(Collectors.toList()));

        return response;
    }

    private void validateLimit(Event event, RequestsChangeStatusRequestDto dto) throws ConflictException {
        int confirmedRequestsCount = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        int availableSlotsCount = event.getParticipantLimit() - confirmedRequestsCount;
        if (availableSlotsCount < dto.getRequestIds().size()) {
            throw new ConflictException("The limit has been reached");
        }
    }
}
