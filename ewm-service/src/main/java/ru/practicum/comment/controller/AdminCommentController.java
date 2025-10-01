package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.services.interfaces.AdminCommentService;
import ru.practicum.common.exception.NotFoundException;

@RestController
@RequestMapping("/admin/events/{eventId}/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(name = "eventId") Long eventId,
                              @PathVariable(name = "commentId") Long commentId) throws NotFoundException {
        adminCommentService.deleteComment(eventId, commentId);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto getCommentById(@PathVariable(name = "eventId") Long eventId,
                                         @PathVariable(name = "commentId") Long commentId) throws NotFoundException {
        return adminCommentService.getCommentById(eventId, commentId);
    }
}