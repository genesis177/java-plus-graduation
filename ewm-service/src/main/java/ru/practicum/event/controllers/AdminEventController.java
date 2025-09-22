package ru.practicum.event.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.exception.ConflictException;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.dto.EventAdminUpdateDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.services.interfaces.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminEventService adminEventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@RequestBody @Valid EventAdminUpdateDto eventAdminUpdateDto,
                                           @PathVariable(name = "eventId") @Positive long eventId)
            throws NotFoundException, BadRequestException, ConflictException {
        return adminEventService.updateEvent(eventAdminUpdateDto, eventId);
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,

                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeEnd,

                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) throws BadRequestException {
        return adminEventService.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
