package ru.practicum.service;


import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentUpdateDto;
import ru.practicum.dto.event.EventWithCommentsDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsAdmin(Integer size, Integer from);

    void deleteCommentByAdmin(Long commentId);

    CommentDto createComment(CommentCreateDto commentCreateDto, Long userId, Long eventId);

    List<CommentDto> getAllCommentsByUserId(Long userId);

    CommentDto updateComment(Long commentId, CommentUpdateDto commentUpdateDto, Long userId, Long eventId);

    void deleteCommentByUserId(Long userId, Long eventId, Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    EventWithCommentsDto getEventWithComments(Long eventId);
}