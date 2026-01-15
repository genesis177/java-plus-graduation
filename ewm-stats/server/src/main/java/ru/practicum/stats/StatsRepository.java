package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("""
            SELECT new ru.practicum.dto.StatsDto(app, uri, COUNT(DISTINCT ip))
            FROM Hit AS h
            WHERE h.timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR h.uri IN :uris)
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<StatsDto> calculateUniqueStats(
            @Param("uris") List<String> uris,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT new ru.practicum.dto.StatsDto(app, uri, COUNT(ip))
            FROM Hit AS h
            WHERE h.timestamp BETWEEN :start AND :end
            AND (:uris IS NULL OR h.uri IN :uris)
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h.ip) DESC
            """)
    List<StatsDto> calculateStats(
            @Param("uris") List<String> uris,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
