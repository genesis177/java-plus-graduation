package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public HitDto createHit(HitDto requestDto) {
        log.debug("Сохраняем запрос hit: app={}, uri={}, ip={}, timestamp={}",
                requestDto.getApp(), requestDto.getUri(),
                requestDto.getIp(), requestDto.getTimestamp());
        Hit savedHit = statsRepository.save(HitMapper.toHit(requestDto));
        log.debug("Hit успешно сохранен с ID: {}", savedHit.getId());
        return HitMapper.toHitDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    ) throws BadRequestException {
        log.info("Получен запрос на получение статистики с параметрами: start='{}', end='{}', uris={}, unique={}", start, end, uris, unique);

        if (start == null) {
            log.info("Не указано начало диапазона.");
            throw new BadRequestException("Не указано начало диапазона.");
        }

        if (end == null) {
            log.info("Не указан конец диапазона.");
            throw new BadRequestException("Не указан конец диапазона.");
        }

        if (start.isAfter(end)) {
            log.warn("Ошибка в датах: дата начала {} после даты окончания {}", start, end);
            throw new BadRequestException("Дата начала не должна быть позже даты окончания");
        }

        List<StatsDto> result;
        if (unique) {
            log.debug("Запросы статистики для уникальных uri");
            result = statsRepository.calculateUniqueStats(uris, start, end);
        } else {
            log.debug("Запросы статистики для неуникальных uri");
            result = statsRepository.calculateStats(uris, start, end);
        }

        log.debug("Получен результат: {}", result);
        return result;
    }
}
