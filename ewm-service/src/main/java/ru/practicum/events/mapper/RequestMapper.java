package ru.practicum.events.mapper;

import ru.practicum.events.model.Request;
import ru.practicum.events.model.dto.ParticipationRequestDto;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();

    }
}
