package ru.practicum.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CategoryMapper {

    public static CategoryDto mapToCategoryDto(Category category) {
        log.info("Преобразование модели БД {} в модель {}", Category.class, CategoryDto.class);
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
