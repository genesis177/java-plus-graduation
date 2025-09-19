package ru.practicum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.CategoryDto;

@Component
@Slf4j
public class CategoryMapper {

    public static CategoryDto mapToCategoryDto(ru.practicum.event.Category category) {
        log.info("Преобразование модели БД {} в модель {}", ru.practicum.event.Category.class, CategoryDto.class);
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}