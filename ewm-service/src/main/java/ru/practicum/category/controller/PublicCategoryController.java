package ru.practicum.category.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.common.exception.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size,
                                           HttpServletResponse response) {
        Page<CategoryDto> page = categoryService.getCategories(from, size);
        //Возвращаем количество элементов в заголовке из-за тестов, они требуют список пользователей
        response.setHeader("X-Total-Count", String.valueOf(page.getTotalElements()));

        return page.getContent();
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long catId) throws NotFoundException {
        return categoryService.getCategoryById(catId);
    }
}