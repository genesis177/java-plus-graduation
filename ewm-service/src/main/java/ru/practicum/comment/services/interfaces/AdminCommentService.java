package ru.practicum.comment.services.interfaces;

import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.common.exception.NotFoundException;

public interface AdminCommentService {

    /**
     * Удаление комментария администратором
     *
     * @param eventId   идентификатор события
     * @param commentId идентификатор комментария
     */
    void deleteComment(Long eventId, Long commentId) throws NotFoundException;

    /**
     * Получение комментария по идентификатору администратором
     *
     * @param eventId   идентификатор события
     * @param commentId идентификатор комментария
     * @return данные комментария
     */
    CommentFullDto getCommentById(Long eventId, Long commentId) throws NotFoundException;
}