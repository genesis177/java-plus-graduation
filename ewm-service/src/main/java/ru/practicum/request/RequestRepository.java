package ru.practicum.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findAllByRequesterId(Long userId, Pageable pageable);

    boolean existsByRequesterIdAndEventId(Long userId, Long id);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findByIdInAndEventId(List<Long> requestIds, Long eventId);

    int countByEventIdAndStatus(Long eventId, RequestStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Request r SET r.status = :status WHERE r.id IN :ids")
    void updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") RequestStatus status);
}
