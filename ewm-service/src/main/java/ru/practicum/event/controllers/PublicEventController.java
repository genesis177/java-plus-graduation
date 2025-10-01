package ru.practicum.event.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.exception.NotFoundException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.services.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final PublicEventService publicEventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,

                                         @RequestParam(required = false)
                                         LocalDateTime rangeStart,
                                         @RequestParam(required = false)
                                         LocalDateTime rangeEnd,

                                         @RequestParam(defaultValue = "false")
                                         Boolean onlyAvailable,

                                         @RequestParam(required = false)
                                         @Pattern(regexp = "EVENT_DATE|VIEWS")
                                         String sort,

                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,

                                         HttpServletRequest request,
                                         HttpServletResponse response) throws BadRequestException {

        Page<EventShortDto> page = publicEventService.getEventsWithFilters(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);

        response.setHeader("X-Total-Count", String.valueOf(page.getTotalElements()));

        return page.getContent();
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable @Positive Long eventId,
                                     HttpServletRequest request) throws NotFoundException, BadRequestException {
        return publicEventService.getEventById(eventId, request);
    }
}
