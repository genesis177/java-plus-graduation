package ru.practicum.comment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.Comment;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.CommentRepository;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.services.interfaces.PublicCommentService;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.enums.States;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublicCommentServiceImpl implements PublicCommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentFullDto> getCommentsByEventId(Long eventId, int from, int size) throws NotFoundException {
        log.info("Получение комментариев к событию: eventId={}, from={}, size={}", eventId, from, size);

        // Проверяем существование события
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        // Проверяем, что событие опубликовано (неавторизованные пользователи могут видеть только опубликованные события)
        if (!event.getState().equals(States.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " is not published");
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));

        Page<Comment> commentsPage = commentRepository.findAllByEventId(eventId, pageable);
        log.info("Найдено {} комментариев для события с id={}", commentsPage.getTotalElements(), eventId);

        return commentsPage.map(CommentMapper::mapToCommentFullDto);
    }
}