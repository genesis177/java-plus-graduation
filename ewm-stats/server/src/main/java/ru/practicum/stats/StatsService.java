package ru.practicum.stats;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.util.List;

public interface StatsService {

    HitDto createHit(HitDto requestDto);

    List<StatsDto> getStats(
            String start,
            String end,
            List<String> uris,
            Boolean unique
    );
}
