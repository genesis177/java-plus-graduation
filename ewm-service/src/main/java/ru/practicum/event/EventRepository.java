package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiatorId(Long id, Pageable pageable);

    @Query("""
                SELECT e
                FROM Event AS e
                WHERE (?1 IS NULL OR e.initiator.id IN ?1)
                AND (?2 IS NULL OR e.state IN ?2)
                AND (?3 IS NULL OR e.category.id IN ?3)
                AND (CAST(?4 AS timestamp) IS NULL OR e.eventDate >= ?4)
                AND (CAST(?5 AS timestamp) IS NULL OR e.eventDate < ?5)
            """)
    List<Event> findAllByFiltersAdmin(List<Long> users, List<String> states, List<Long> categories,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    boolean existsByCategoryId(Long categoryId);
}
