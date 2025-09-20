package ru.practicum.category;

import ru.practicum.common.exception.NotFoundException;

public interface CategoryService {

    Category findById(long id) throws NotFoundException;
}