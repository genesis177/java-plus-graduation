package ru.practicum.category.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.common.exception.AlreadyExistsException;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.EventRepository;
import ru.practicum.event.services.interfaces.PrivateEventService;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto dto) throws AlreadyExistsException {
        log.info("Проверка dto категории: {}", dto);
        if (repository.existsByName(dto.getName())) {
            throw new AlreadyExistsException("Категория с именем " + dto.getName() + " уже существует");
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
    public CategoryDto getCategoryById(Long id) throws NotFoundException {
        log.info("Получить категорию по id: {}", id);

        return repository.findById(id)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new NotFoundException("Категория с id " + id + " не найдена"));
    }

    @Override
    public CategoryDto updateCategory(Long id, NewCategoryDto dto) throws NotFoundException, AlreadyExistsException {
        log.info("Обновить категорию: {}", dto);
        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id " + id + " не найдена"));

        if (repository.existsByName(dto.getName()) && !category.getName().equals(dto.getName())) {
            log.warn("Не удалось обновить категорию. Имя '{}' уже существует.", dto.getName());
            throw new AlreadyExistsException("Имя категории уже существует.");
        }

        category.setName(dto.getName());
        repository.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Long id) throws NotFoundException, ConflictException {
        log.info("Удалить категорию по id: {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Категория с id " + id + " не найдена");
        }
        if (eventRepository.existsByCategoryId(id)) {
            log.warn("Категория с id {} используется событием и не может быть удалена.", id);
            throw new ConflictException("Не может быть удалена; используется событием.");
        }
        repository.deleteById(id);
    }

    @Override
    public Category findById(Long id) throws NotFoundException{
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException("Категория с id " + id + " не найдена"));
    }
}