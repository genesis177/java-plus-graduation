package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected final RestTemplate rest;

    @Value("${stats-server.url:${STATS_SERVER_URL:http://stats-server:9090}}")
    private String statsServerUrl;

    public ResponseEntity<HitDto> postHit(HitDto endpointHitDto) {
        String url = statsServerUrl + "/hit";
        return rest.postForEntity(url, endpointHitDto, HitDto.class);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String startStr = start.format(FORMATTER);
        String endStr = end.format(FORMATTER);

        StringBuilder urlBuilder = new StringBuilder(statsServerUrl)
                .append("/stats?start=").append(startStr)
                .append("&end=").append(endStr)
                .append("&unique=").append(unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                urlBuilder.append("&uris=").append(uri);
            }
        }

        String url = urlBuilder.toString();
        ResponseEntity<StatsDto[]> response = rest.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                StatsDto[].class
        );
        return Arrays.asList(response.getBody());
    }
}
