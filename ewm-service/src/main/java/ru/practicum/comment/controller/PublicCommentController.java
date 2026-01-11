package ru.practicum.comment.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.comment.services.interfaces.PublicCommentService;
import ru.practicum.common.exception.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final PublicCommentService publicCommentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getCommentsByEventId(@PathVariable(name = "eventId") Long eventId,
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @RequestParam(name = "size", defaultValue = "10") int size,
                                                     HttpServletResponse response) throws NotFoundException {
        Page<CommentFullDto> page = publicCommentService.getCommentsByEventId(eventId, from, size);
        response.setHeader("X-Total-Count", String.valueOf(page.getTotalElements()));

        return page.getContent();
    }
}