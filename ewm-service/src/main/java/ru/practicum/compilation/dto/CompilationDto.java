package ru.practicum.compilation.dto;

import lombok.*;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    /**
     * Идентификатор
     */
    private Long id;

    /**
     * События входящие в подборку
     */
    private List<EventShortDto> events;

    /**
     * Закреплена ли подборка на главной странице сайта
     */
    private Boolean pinned;

    /**
     * Заголовок подборки
     */
    private String title;
}