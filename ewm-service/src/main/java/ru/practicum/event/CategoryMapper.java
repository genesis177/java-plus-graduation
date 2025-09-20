package ru.practicum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.event.dto.CategoryDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CategoryMapper {

    public static Category toCategory(NewCategoryDto dto) {
        log.info("Преобразование {} в модель {}", NewCategoryDto.class, Category.class);
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        log.info("Преобразование модели БД {} в модель {}", Category.class, CategoryDto.class);
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        log.info("Преобразование списка {} в список {}", Category.class, CategoryDto.class);
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}