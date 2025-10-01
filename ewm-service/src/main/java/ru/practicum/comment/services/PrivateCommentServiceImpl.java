package ru.practicum.comment.services;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.Comment;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.CommentRepository;
import ru.practicum.comment.dto.CommentCreateOrUpdateDto;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.services.interfaces.PrivateCommentService;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.enums.States;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentFullDto createComment(Long userId, Long eventId, CommentCreateOrUpdateDto dto) throws
                                                                                                 NotFoundException,
                                                                                                 ConflictException {
        log.info("Создание комментария на уровне сервиса");

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        log.info("Передан идентификатор автора комментария: {}", author.getId());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        log.info("Передан идентификатор комментируемого события: {}", event.getId());

        Comment comment = CommentMapper.mapToComment(dto);
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        log.info("Сохраняемая модель дополнена данными");

        log.info("Валидация несохраненной модели");
        validateComment(comment);
        log.info("Валидация несохраненной модели завершена");

        commentRepository.save(comment);
        log.info("Сохранения завершено. Получен идентификатор {}", comment.getId());

        CommentFullDto result = CommentMapper.mapToCommentFullDto(comment);
        log.info("Сохраненная модель преобразована. Идентификатор модели после преобразования {}", result.getId());

        log.info("Возврат результатов создания на уровень контроллера");
        return result;
    }

    @Override
    public CommentFullDto updateComment(Long userId, Long eventId, Long commentId, CommentCreateOrUpdateDto dto) throws
                                                                                                                 NotFoundException,
                                                                                                                 ConflictException {
        log.info("Обновление комментария на уровне сервиса");

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        log.info("Передан идентификатор автора обновляемого комментария: {}", author.getId());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        log.info("Передан идентификатор события обновляемого комментария: {}", event.getId());

        Comment comment = commentRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        log.info("Передан идентификатор обновляемого комментария: {}", comment.getId());

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new ConflictException("Field: author. Error: пользователь с id=" + author.getId()
                    + " не является автором комментария с id=" + comment.getId());
        }

        CommentMapper.updateFields(comment, dto);
        log.info("Обновляемая модель пополнена данными");

        log.info("Валидация обновляемой модели");
        validateComment(comment);
        log.info("Валидация обновляемой модели завершена");

        commentRepository.save(comment);
        log.info("Изменение завершено");

        CommentFullDto result = CommentMapper.mapToCommentFullDto(comment);
        log.info("Измененная модель преобразована. Идентификатор после преобразования {}", result.getId());

        log.info("Возврат результатов изменения на уровень контроллера");
        return result;
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) throws NotFoundException, ConflictException {
        log.info("Удаление комментария на уровне сервиса");

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        log.info("Передан идентификатор автора удаляемого комментария: {}", author.getId());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        log.info("Передан идентификатор события удаляемого комментария: {}", event.getId());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        log.info("Передан идентификатор удаляемого комментария: {}", comment.getId());

        if (!comment.getEvent().getId().equals(event.getId())) {
            throw new ConflictException(
                    "Field: event. Error: комментарий не относится к событию с id=" + event.getId());
        }

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new ConflictException("Field: author. Error: пользователь с id=" + author.getId()
                    + " не является автором комментария с id=" + comment.getId());
        }

        commentRepository.deleteById(comment.getId());
        log.info("Удаление модели завершено");

        log.info("Возврат результатов удаления на уровень контроллера");
    }

    /**
     * Метод проверяет правильность заполнения полей комментария
     *
     * @param comment комментарий
     * @throws ConflictException если нарушены ограничения возможности комментирования
     */
    private void validateComment(Comment comment) throws ConflictException {
        log.info("Валидация комментируемого события");
        validateEvent(comment);
        log.info("Валидация комментируемого события завершена");
    }

    /**
     * Метод проверяет возможность комментирования события
     *
     * @param comment комментарий
     * @throws ConflictException если нарушены ограничения возможности комментирования
     */
    private void validateEvent(Comment comment) throws ConflictException {
        if (!comment.getEvent().getState().equals(States.PUBLISHED)) {
            throw new ConflictException(
                    "Field: event. Error: комментируемое событие должно быть опубликовано. Value: " + comment.getEvent()
                            .getState());
        }
    }
}