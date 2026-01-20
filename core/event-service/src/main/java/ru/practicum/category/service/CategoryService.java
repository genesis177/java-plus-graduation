package ru.practicum.category.service;

import ru.practicum.dto.category.CategoryRequestDto;
import ru.practicum.dto.category.CategoryResponseDto;

import java.util.Collection;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto category);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto category);

    void deleteCategory(Long id);

    Collection<CategoryResponseDto> getCategories(Integer from, Integer size);

    CategoryResponseDto getCategoryById(Long id);
}