package ru.practicum.comment;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.comment.dto.CommentCreateOrUpdateDto;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.event.EventMapper;
import ru.practicum.user.UserMapper;

@UtilityClass
@Slf4j
public class CommentMapper {

    public Comment mapToComment(CommentCreateOrUpdateDto dto) {
        log.debug("Преобразование модели {} в модель {} для сохранения", CommentCreateOrUpdateDto.class, Comment.class);
        return Comment.builder()
                .text(dto.getText())
                .build();
    }

    public CommentFullDto mapToCommentFullDto(Comment comment) {
        log.debug("Преобразование модели {} в модель {}", Comment.class, CommentFullDto.class);
        return CommentFullDto.builder()
                .id(comment.getId())
                .author(UserMapper.mapToUserShortDto(comment.getAuthor()))
                .event(EventMapper.mapToEventShortDto(comment.getEvent()))
                .text(comment.getText())
                .created(comment.getCreatedOn())
                .build();
    }

    public void updateFields(Comment comment, CommentCreateOrUpdateDto dto) {
        log.debug("Преобразование модели {} в модель {} для обновления", CommentCreateOrUpdateDto.class, Comment.class);
        if (!(dto.getText() == null || dto.getText().trim().isBlank())) {
            comment.setText(dto.getText());
        }
    }
}