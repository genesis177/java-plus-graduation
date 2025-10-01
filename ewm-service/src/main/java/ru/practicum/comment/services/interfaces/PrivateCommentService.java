package ru.practicum.comment.services.interfaces;

import jakarta.validation.Valid;
import ru.practicum.comment.dto.CommentCreateOrUpdateDto;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;

public interface PrivateCommentService {

    /**
     * Метод получает несохранённый экземпляр класса {@link CommentCreateOrUpdateDto}, проверяет его, передает для
     * сохранения и возвращает экземпляр класса {@link CommentFullDto} после сохранения
     *
     * @param userId идентификатор автора комментария
     * @param eventId идентификатор комментируемого события
     * @param dto данные добавляемого комментария
     * @return данные добавленного комментария
     */
    CommentFullDto createComment(Long userId, Long eventId, @Valid CommentCreateOrUpdateDto dto) throws
                                                                                                 NotFoundException,
                                                                                                 ConflictException;

    /**
     * Метод проверяет и передаёт для сохранения экземпляр класса {@link CommentCreateOrUpdateDto}
     *
     * @param userId идентификатор автора комментария
     * @param eventId идентификатор комментируемого события
     * @param commentId идентификатор комментария
     * @param dto данные обновляемого комментария
     * @return данные обновленного комментария
     */
    CommentFullDto updateComment(Long userId, Long eventId, Long commentId, @Valid CommentCreateOrUpdateDto dto) throws
                                                                                                                 NotFoundException,
                                                                                                                 ConflictException;

    /**
     * Метод проверяет возможность удаления комментария и передаёт для удаления по идентификатору
     *
     * @param userId идентификатор автора комментария
     * @param eventId идентификатор комментируемого события
     * @param commentId идентификатор комментария
     */
    void deleteComment(Long userId, Long eventId, Long commentId) throws NotFoundException, ConflictException;
}