package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.model.dto.EventFullDto;
import ru.practicum.events.model.dto.UpdateEventAdminRequest;
import ru.practicum.events.model.enums.EventState;
import ru.practicum.events.service.EventsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class AdminEventsController {

    private final EventsService eventsService;

    @GetMapping
    public List<EventFullDto> getEventsAdmin(
            @RequestParam(required = false) List<Integer> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventsService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventByAdmin(@PathVariable Integer eventId,
                                          @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return eventsService.updateEventByIdAdmin(eventId, updateEventAdminRequest);
    }
}
