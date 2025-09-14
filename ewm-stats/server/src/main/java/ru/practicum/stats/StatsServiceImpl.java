package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.common.exception.ErrorException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public HitDto createHit(HitDto requestDto) {
        Hit hit = HitMapper.toHit(requestDto);
        Hit savedHit = statsRepository.save(hit);

        return HitMapper.toHitDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(
            String start,
            String end,
            List<String> uris,
            Boolean unique
    ) {

        if (start == null) {
            throw new ErrorException("Не указано начало диапазона.");
        }

        if (end == null) {
            throw new ErrorException("Не указан конец диапазона.");
        }

        LocalDateTime startDateTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDateTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (startDateTime.isAfter(endDateTime)) {
            throw new ErrorException("Начало диапазона не может превышать конец.");
        }

        boolean isUnique = Boolean.TRUE.equals(unique);
        List<ViewStats> viewStats;

        if (isUnique) {
            viewStats = statsRepository.calculateUniqueStats(uris, startDateTime, endDateTime);
        } else {
            viewStats = statsRepository.calculateStats(uris, startDateTime, endDateTime);
        }

        return viewStats.stream()
                .map(HitMapper::toStatsResponseDto)
                .collect(Collectors.toList());
    }
}
