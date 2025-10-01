package ru.practicum.category;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

@UtilityClass
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toCategory(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }

    public Category toCategory(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        return categories.stream()
                .map(CategoryMapper::toCategoryDto)
                .toList();
    }
}