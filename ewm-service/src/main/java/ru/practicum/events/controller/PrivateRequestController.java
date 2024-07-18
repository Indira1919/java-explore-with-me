package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.model.dto.ParticipationRequestDto;
import ru.practicum.events.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getEventRequestsByRequester(@PathVariable Integer userId) {
        return requestService.getEventRequestsByRequester(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addEventRequest(@PathVariable Integer userId,
                                                   @RequestParam Integer eventId) {
        return requestService.addEventRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequest(@PathVariable Integer userId,
                                                      @PathVariable Integer requestId) {
        return requestService.cancelEventRequest(userId, requestId);
    }
}
