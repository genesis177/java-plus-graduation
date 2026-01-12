package ru.practicum.comments.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/comments")
public class CommentsAdminController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentsAdmin(@RequestParam Integer size, @RequestParam Integer from) {
        return commentService.getCommentsAdmin(size, from);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}