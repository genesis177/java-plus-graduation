package ru.practicum.category.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.*;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.Category;
import ru.practicum.event.EventRepository;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) {
        log.info("Проверка dto категории: {}", dto);
        if (repository.existsByName(dto.getName())) {
            throw new RuntimeException("Категория с именем " + dto.getName() + " уже существует");
        }

        Category category = CategoryMapper.toCategory(dto);
        repository.save(category);
        log.info("Категория сохранена: {}", category);

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategory(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Получить все категории с пагинацией from={}, size={}", from, size);

        Page<Category> categoryPage = repository.findAll(pageable);
        return CategoryMapper.toCategoryDtoList(categoryPage.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        log.info("Получить категорию по id: {}", id);

        return repository.findById(id)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new RuntimeException("Категория с id " + id + " не найдена"));
    }

    @Override
    public CategoryDto updateCategory(Long id, NewCategoryDto dto) {
        log.info("Обновить категорию: {}", dto);
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория с id " + id + " не найдена"));

        if (repository.existsByName(dto.getName()) && !category.getName().equals(dto.getName())) {
            log.warn("Не удалось обновить категорию. Имя '{}' уже существует.", dto.getName());
            throw new RuntimeException("Имя категории уже существует.");
        }

        category.setName(dto.getName());
        repository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Удалить категорию по id: {}", id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Категория с id " + id + " не найдена");
        }
        if (eventRepository.existsByCategoryId(id)) {
            log.warn("Категория с id {} используется событием и не может быть удалена.", id);
            throw new RuntimeException("Не может быть удалена; используется событием.");
        }
        repository.deleteById(id);
    }
}