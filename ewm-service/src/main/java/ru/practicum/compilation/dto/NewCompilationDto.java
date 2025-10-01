package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    /**
     * Список id событий входящих в подборку
     */
    private Set<Long> events;

    /**
     * Закреплена ли подборка на главной странице сайта
     */
    @Builder.Default
    private Boolean pinned = false;

    /**
     * Заголовок подборки
     */
    @NotBlank(message = "Field: title. Error: must not be blank. Value: null")
    @Size(min = 1, max = 50)
    private String title;
}