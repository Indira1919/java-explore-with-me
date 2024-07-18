package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.mapper.RequestMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Request;
import ru.practicum.events.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.events.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.events.model.dto.ParticipationRequestDto;
import ru.practicum.events.model.enums.EventState;
import ru.practicum.events.model.enums.RequestStatus;
import ru.practicum.events.model.enums.RequestStatusAction;
import ru.practicum.events.repository.RequestRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserServiceImpl userService;
    private final EventsService eventsService;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getEventRequestsByRequester(Integer userId) {

        userService.getUserById(userId);

        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto addEventRequest(Integer userId, Integer eventId) {

        User user = userService.getUserById(userId);
        Event event = eventsService.getEvent(eventId);

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Вы не можете создать запрос на собственное мероприятие");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Вы не можете создать запрос на неопубликованное мероприятие");
        }

        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new ConflictException("Ошибка, повторный запрос");
        }

        if (event.getParticipantLimit() != 0 &&
                requestRepository.findAllByEventIdAndStatusEquals(event.getId(), RequestStatus.CONFIRMED).size()
                        >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит подтвержденных заявок на участие достигнут");
        }

        Request newRequest = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        Request request = requestRepository.save(newRequest);

        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequest(Integer userId, Integer requestId) {

        User user = userService.getUserById(userId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Запрос не найден"));

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestOfEvent(Integer userId, Integer eventId) {

        User user = userService.getUserById(userId);
        Event event = eventsService.getEvent(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Вы не являетесь организатором");
        }

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsByEventOwner(Integer userId, Integer eventId,
                                                                          EventRequestStatusUpdateRequest event) {
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        User user = userService.getUserById(userId);
        Event event1 = eventsService.getEvent(eventId);

        int conf = requestRepository
                .findAllByEventIdAndStatusEquals(event1.getId(), RequestStatus.CONFIRMED).size();

        if (!userId.equals(event1.getInitiator().getId())) {
            throw new ConflictException("Вы не являетесь организатором");
        }

        if (!event1.getRequestModeration() ||
                event1.getParticipantLimit() == 0 ||
                event.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<Request> requests = requestRepository.findAllByIdIn(event.getRequestIds());

        if (requests.size() != event.getRequestIds().size()) {
            throw new ObjectNotFoundException("Ошибка запроса. Запрос не найден");
        }

        if (event1.getParticipantLimit() <= conf) {
            throw new ConflictException("Лимит участия достигнут");
        }

        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("Статус всех заявок должен быть PENDING");
        }

        if (event.getStatus().equals(RequestStatusAction.REJECTED)) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            requestRepository.saveAll(requests);
            rejected.addAll(requests);
        } else {
            int freePlaces = event1.getParticipantLimit() - conf;

            if (freePlaces > 0 && freePlaces >= requests.size()) {
                requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                requestRepository.saveAll(requests);
                confirmed.addAll(requests);
            } else {
                requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                requestRepository.saveAll(requests);
                rejected.addAll(requests);
            }
        }

        return new EventRequestStatusUpdateResult(
                confirmed
                        .stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()),
                rejected
                        .stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()));
    }
}
