package ru.practicum.comment.services.interfaces;

import org.springframework.data.domain.Page;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.common.exception.NotFoundException;

public interface PublicCommentService {

    /**
     * Получение списка комментариев к событию
     *
     * @param eventId идентификатор события
     * @param from    номер начального элемента
     * @param size    размер страницы
     * @return страница с комментариями
     */
    Page<CommentFullDto> getCommentsByEventId(Long eventId, int from, int size) throws NotFoundException;
}