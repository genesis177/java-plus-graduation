package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.common.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public Category findById(long id) throws NotFoundException {

        return categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Категория с id " + id + " не найдена"));
    }
}
