package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.model.dto.*;
import ru.practicum.events.service.EventsService;
import ru.practicum.events.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {
    private final EventsService eventsService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getEventsOfUser(@PathVariable Integer userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventsService.getEventsOfUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventsById(@PathVariable Integer userId,
                                      @PathVariable Integer eventId) {
        return eventsService.getEventsById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestOfEvent(@PathVariable Integer userId,
                                                           @PathVariable Integer eventId) {
        return requestService.getRequestOfEvent(userId, eventId);

    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsByEventOwner(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.updateEventRequestsByEventOwner(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventPrivate(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventsService.updateEventPrivate(userId, eventId, updateEventUserRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEventPrivate(@PathVariable Integer userId,
                                        @Valid @RequestBody NewEventDto newEventDto) {
        return eventsService.addEventPrivate(userId, newEventDto);
    }
}
