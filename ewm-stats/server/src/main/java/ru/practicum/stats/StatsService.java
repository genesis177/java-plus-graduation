package ru.practicum.stats;

import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.HitResponseDto;
import ru.practicum.dto.StatsResponseDto;

import java.util.List;

public interface StatsService {

    HitResponseDto createHit(HitRequestDto requestDto);

    List<StatsResponseDto> getStats(
            String start,
            String end,
            List<String> uris,
            Boolean unique
    );
}
