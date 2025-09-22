package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long userId);

    Integer countByEventId(Long eventId);

    boolean existsByRequesterIdAndEventId(Long userId, Long id);

    List<Request> findAllByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findByIdAndEventId(List<Long> requestIds, Long eventId);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    @Modifying
    @Query("UPDATE Request r SET r.status = :status WHERE r.id IN :ids")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") RequestStatus status);
}
