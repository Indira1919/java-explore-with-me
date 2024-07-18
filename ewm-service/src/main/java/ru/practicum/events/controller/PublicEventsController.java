package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.model.dto.EventFullDto;
import ru.practicum.events.model.dto.EventShortDto;
import ru.practicum.events.model.enums.SortState;
import ru.practicum.events.service.EventsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventsController {
    private final EventsService eventsService;

    @GetMapping
    public List<EventShortDto> getEventsPublic(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Integer> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeStart,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(defaultValue = "EVENT_DATE") SortState sort,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        return eventsService.getEventsPublic(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByIdPublic(@PathVariable Integer id, HttpServletRequest request) {
        return eventsService.getEventByIdPublic(id, request);
    }
}