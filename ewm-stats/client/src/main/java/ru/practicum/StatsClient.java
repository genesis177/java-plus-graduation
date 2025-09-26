package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsClient {
    protected final RestTemplate rest;

    public ResponseEntity<Object> postHit(HitDto hitDto) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, hitDto, new ParameterizedTypeReference<Object>() {
        });
    }

    public ResponseEntity<List<StatsDto>> getStats(LocalDateTime start,
                                                   LocalDateTime end,
                                                   List<String> uris,
                                                   Boolean unique) {
        Map<String, Object> parameters = Map.of("start", start, "end", end, "uris", uris, "unique", unique);
        return makeAndSendRequest(HttpMethod.GET, "/stats", parameters,
                null, new ParameterizedTypeReference<List<StatsDto>>() {});
    }

    private <T> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters, Object body,
                                                          ParameterizedTypeReference<T> responseType) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders());
        try {
            if (parameters != null) {
                return rest.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                return rest.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}