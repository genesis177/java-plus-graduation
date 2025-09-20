package ru.practicum.category.service;

import java.util.List;

import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.common.exception.AlreadyExistsException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.dto.CategoryDto;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto) throws AlreadyExistsException;

    List<CategoryDto> getCategory(int from, int size);

    CategoryDto getCategoryById(Long id) throws NotFoundException;

    CategoryDto updateCategory(Long id, NewCategoryDto dto) throws NotFoundException, AlreadyExistsException;

    void deleteCategory(Long id) throws NotFoundException, ConflictException;
}