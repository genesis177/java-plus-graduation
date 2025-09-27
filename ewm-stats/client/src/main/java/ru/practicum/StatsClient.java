package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;


import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsClient {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected final RestTemplate rest;

    @Value("${stats-server.url}")
    private String statsServerUrl;

    public ResponseEntity<Object> postHit(HitDto hitDto) {
        URI uri = UriComponentsBuilder.fromHttpUrl(statsServerUrl)
                .path("/hit")
                .build()
                .toUri();
        return makeAndSendRequest(HttpMethod.POST, uri, null, hitDto, new ParameterizedTypeReference<Object>() {
        });
    }

    public ResponseEntity<List<StatsDto>> getStats(LocalDateTime start,
                                                   LocalDateTime end,
                                                   List<String> uris,
                                                   Boolean unique) {
        log.info("Получен запрос статистики start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(statsServerUrl)
                .path("/stats")
                .queryParam("start", start.format(FORMATTER))
                .queryParam("end", end.format(FORMATTER))
                .queryParam("unique", unique);

        if (uris != null) {
            for (String u : uris) {
                builder.queryParam("uris", u);
            }
        }

        URI uri = builder.encode().build().toUri();
        return makeAndSendRequest(HttpMethod.GET, uri, null,
                null, new ParameterizedTypeReference<>() {
                });
    }

    private <T> ResponseEntity<T> makeAndSendRequest(HttpMethod method, URI uri,
                                                     @Nullable Map<String, Object> parameters, Object body,
                                                     ParameterizedTypeReference<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders());
        try {
            ResponseEntity<T> response = rest.exchange(uri, method, requestEntity, responseType);
            return response;
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
