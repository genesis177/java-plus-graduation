package ru.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Collection<Request> findAllByRequesterId(long userId);

    @Query("""
            SELECT COUNT(*) FROM Request r
            WHERE r.event_id = :event_id
            """)
    Integer getParticipantCount(@Param("eventId") Long eventId);
}
