package ru.practicum.compilation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Page<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);

    boolean existsByTitle(String title);
}