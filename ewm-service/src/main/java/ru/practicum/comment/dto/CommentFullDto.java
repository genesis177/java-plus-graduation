package ru.practicum.comment.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentFullDto {

    private Long id;
    private UserShortDto author;
    private EventShortDto event;
    private String text;
    private LocalDateTime created;
}
