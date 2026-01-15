package ru.practicum.service.stats;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsDtoResponse {
    String app;
    String uri;
    Long hits;
}