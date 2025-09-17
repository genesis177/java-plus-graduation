package ru.practicum.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@Slf4j
@AllArgsConstructor
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") int from,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Request to get categories with from={}, size={}", from, size);
        return categoryService.getCategory(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Request to get category by id: {}", catId);
        return categoryService.getCategoryById(catId);
    }
}