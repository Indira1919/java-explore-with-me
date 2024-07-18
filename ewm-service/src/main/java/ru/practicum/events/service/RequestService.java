package ru.practicum.events.service;

import ru.practicum.events.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.events.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.events.model.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getEventRequestsByRequester(Integer userId);

    ParticipationRequestDto addEventRequest(Integer userId, Integer eventId);

    ParticipationRequestDto cancelEventRequest(Integer userId, Integer requestId);

    List<ParticipationRequestDto> getRequestOfEvent(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateEventRequestsByEventOwner(Integer userId,
                                                                   Integer eventId,
                                                                   EventRequestStatusUpdateRequest event);
}
