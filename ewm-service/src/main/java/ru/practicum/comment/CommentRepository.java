package ru.practicum.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Получение комментариев к событию с пагинацией
     *
     * @param eventId  идентификатор события
     * @param pageable параметры пагинации
     * @return страница комментариев
     */
    Page<Comment> findAllByEventId(Long eventId, Pageable pageable);
}