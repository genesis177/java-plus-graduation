package ru.practicum.stats;

import org.apache.coyote.BadRequestException;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    HitDto createHit(HitDto requestDto);

    List<StatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    ) throws BadRequestException;
}
