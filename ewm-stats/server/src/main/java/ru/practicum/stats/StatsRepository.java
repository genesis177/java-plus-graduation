package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("""
            SELECT new ru.practicum.stats.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM Hit h
            WHERE uri IN :uris AND (timestamp >= :start AND timestamp < :end) GROUP BY h.app, h.uri
            """)
    List<ViewStats> calculateUniqueStats(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.stats.ViewStats(h.app, h.uri, COUNT(h.ip))
            FROM Hit h
            WHERE uri IN :uris AND (timestamp >= :start AND timestamp < :end) GROUP BY h.app, h.uri
            """)
    List<ViewStats> calculateStats(List<String> uris, LocalDateTime start, LocalDateTime end);
}
