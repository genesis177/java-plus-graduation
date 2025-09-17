package ru.practicum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.event.dto.CategoryDto;

@Component
@Slf4j
public class CategoryMapper {

    public static CategoryDto mapToCategoryDto(ru.practicum.category.model.Category category) {
        log.info("Преобразование модели БД {} в модель {}", ru.practicum.category.model.Category.class, CategoryDto.class);
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}