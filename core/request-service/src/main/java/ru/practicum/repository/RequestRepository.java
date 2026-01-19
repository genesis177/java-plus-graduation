package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.request.RequestStatus;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Boolean existsByEventIdAndRequesterIdAndStatusNot(Long event, Long requester, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByEventIdAndStatus(Long event, RequestStatus status);

    List<Request> findAllByEventIdAndIdIn(Long event, List<Long> requestIds);


}