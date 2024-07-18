package ru.practicum.events.service;

import ru.practicum.events.model.Event;
import ru.practicum.events.model.enums.SortState;
import ru.practicum.events.model.enums.EventState;
import ru.practicum.events.model.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventsService {

    List<EventShortDto> getEventsOfUser(Integer userId, Integer from, Integer size);

    EventFullDto getEventsById(Integer userId, Integer eventId);

    EventFullDto updateEventPrivate(Integer userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto addEventPrivate(Integer userId, NewEventDto newEventDto);

    List<EventFullDto> getEventsAdmin(List<Integer> users,
                                      List<EventState> states,
                                      List<Integer> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from,
                                      Integer size);

    EventFullDto updateEventByIdAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEventsPublic(String text,
                                        List<Integer> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        SortState sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request);

    EventFullDto getEventByIdPublic(Integer id, HttpServletRequest request);

    Event getEvent(Integer eventId);
}
