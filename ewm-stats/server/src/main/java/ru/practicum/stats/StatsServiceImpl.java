package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.common.exception.ErrorException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public HitDto createHit(HitDto requestDto) {
        log.info("Поступил запрос на сохранение hit");
        Hit hit = HitMapper.toHit(requestDto);
        Hit savedHit = statsRepository.save(hit);

        log.info("Сохраненный hit: id={}, app={}, uri={}, ip={}, timestamp={}",
                savedHit.getId(), savedHit.getApp(), savedHit.getUri(), savedHit.getIp(), savedHit.getTimestamp());

        return HitMapper.toHitDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(
            String start,
            String end,
            List<String> uris,
            Boolean unique
    ) throws BadRequestException {
        log.info("Получен запрос на получение статистики с параметрами: start='{}', end='{}', uris={}, unique={}", start, end, uris, unique);

        if (start == null) {
            throw new BadRequestException("Не указано начало диапазона.");
        }

        if (end == null) {
            throw new BadRequestException("Не указан конец диапазона.");
        }

        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        try {
            startDateTime = LocalDateTime.parse(start, FORMATTER);
            endDateTime = LocalDateTime.parse(end, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Неверный формат даты. Ожидается yyyy-MM-dd HH:mm:ss");
        }

        if (startDateTime.isAfter(endDateTime)) {
            throw new BadRequestException("Начало диапазона не может превышать конец.");
        }

        boolean isUnique = Boolean.TRUE.equals(unique);

        try {
            List<ViewStats> viewStats;
            if (isUnique) {
                log.info("Вызов метода подсчета уникальных ip uris={}, start={}, end={}", uris, startDateTime, endDateTime);
                viewStats = statsRepository.calculateUniqueStats(uris, startDateTime, endDateTime);
            } else {
                viewStats = statsRepository.calculateStats(uris, startDateTime, endDateTime);
            }

            int count = viewStats == null ? 0 : viewStats.size();

            return (viewStats == null ? List.<StatsDto>of() :
                    viewStats.stream()
                            .map(HitMapper::toStatsResponseDto)
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            log.info("Неожиданная ошибка. Параметры: uris={}, start={}, end={}", uris, startDateTime, endDateTime, e);
            throw new ErrorException("Ошибка при получении статистики");
        }
    }
}
