package ru.practicum.comments.service;


import ru.practicum.comments.dto.CommentCreateDto;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentUpdateDto;
import ru.practicum.event.dto.EventWithCommentsDto;

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