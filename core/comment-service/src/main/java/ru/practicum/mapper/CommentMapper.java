package ru.practicum.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentCreateDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventWithCommentsDto;
import ru.practicum.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true) // генерируется БД
    @Mapping(target = "eventId", ignore = true) // будет выставляться отдельно
    @Mapping(target = "authorId", ignore = true)// будет выставляться отдельно
    Comment toEntity(CommentCreateDto commentCreateDto);

    CommentDto toDto(Comment comment);

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "title", source = "event.title")
    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "eventDate", source = "event.eventDate")
    @Mapping(target = "location", source = "event.location")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "participantLimit", source = "event.participantLimit")
    @Mapping(target = "requestModeration", source = "event.requestModeration")
    @Mapping(target = "confirmedRequests", source = "event.confirmedRequests")
    @Mapping(target = "createdOn", source = "event.createdOn")
    @Mapping(target = "initiator", source = "event.initiator")
    @Mapping(target = "state", source = "event.state")
    @Mapping(target = "publishedOn", source = "event.publishedOn")
    @Mapping(target = "comments", source = "comments")
    EventWithCommentsDto toDto(EventFullDto event, List<CommentDto> comments);
}