package ru.practicum.service.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.service.stats.StatsDtoResponse;
import ru.practicum.service.stats.service.StatsService;
import ru.practicum.service.stats.GetStatsRequest;
import ru.practicum.service.stats.StatsDtoRequest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController implements StatsClient {
    final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody StatsDtoRequest request) {
        log.info("Запрос на добавление данных в статистику");
        statsService.saveHit(request);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsDtoResponse> getStatistics(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  LocalDateTime end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Запрос на получение статистики");
        log.info("Параметры: \nstart = {} \nend = {} \nuris = {} \nunique = {}", start, end, uris, unique);
        return statsService.findStats(GetStatsRequest.of(start, end, uris, unique));
    }
}