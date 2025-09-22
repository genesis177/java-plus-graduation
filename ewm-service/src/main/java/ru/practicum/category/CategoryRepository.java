package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("mainCategoryRepository")
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}