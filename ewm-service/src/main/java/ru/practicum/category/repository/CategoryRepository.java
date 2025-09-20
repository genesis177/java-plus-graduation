package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.Category;

@Repository("mainCategoryRepository")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}