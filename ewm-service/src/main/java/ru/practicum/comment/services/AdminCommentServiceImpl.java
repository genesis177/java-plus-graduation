package ru.practicum.comment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.Comment;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.CommentRepository;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.services.interfaces.AdminCommentService;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void deleteComment(Long eventId, Long commentId) throws NotFoundException {
        log.info("Удаление комментария администратором: eventId={}, commentId={}", eventId, commentId);

        // Проверяем существование события
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        // Проверяем существование комментария
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        // Проверяем, что комментарий относится к указанному событию
        if (!comment.getEvent().getId().equals(eventId)) {
            throw new NotFoundException("Comment with id=" + commentId + " does not belong to event with id=" + eventId);
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий с id={} успешно удален администратором", commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentFullDto getCommentById(Long eventId, Long commentId) throws NotFoundException {
        log.info("Получение комментария администратором: eventId={}, commentId={}", eventId, commentId);

        // Проверяем существование события
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        // Проверяем существование комментария
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        // Проверяем, что комментарий относится к указанному событию
        if (!comment.getEvent().getId().equals(eventId)) {
            throw new NotFoundException("Comment with id=" + commentId + " does not belong to event with id=" + eventId);
        }

        CommentFullDto result = CommentMapper.mapToCommentFullDto(comment);
        log.info("Комментарий с id={} успешно получен администратором", commentId);

        return result;
    }
}