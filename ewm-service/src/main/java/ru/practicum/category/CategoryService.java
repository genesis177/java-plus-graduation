package ru.practicum.category;

import ru.practicum.common.exception.NotFoundException;

public interface CategoryService {

    public Category findById(long id) throws NotFoundException;
}
