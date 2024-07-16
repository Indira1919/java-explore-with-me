package ru.practicum.events.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.mapper.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.service.CategoriesServiceImpl;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.mapper.LocationMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.QEvent;
import ru.practicum.events.model.dto.*;
import ru.practicum.events.model.enums.EventState;
import ru.practicum.events.model.enums.RequestStatus;
import ru.practicum.events.model.enums.SortState;
import ru.practicum.events.repository.EventsRepository;
import ru.practicum.events.repository.LocationRepository;
import ru.practicum.events.repository.RequestRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.model.ViewStats;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventsServiceImpl implements EventsService {
    private final EventsRepository eventsRepository;
    private final UserServiceImpl userService;
    private final CategoriesServiceImpl categoriesService;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsServiceImpl statsService;
    private static final QEvent qEvent = QEvent.event;

    @Override
    public List<EventShortDto> getEventsOfUser(Integer userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        User user = userService.getUserById(userId);

        List<EventShortDto> events = eventsRepository.findAllByInitiator(user, page)
                .stream()
                .map(this::setViewsAndConfirmedRequestsToEventsShortDto)
                .collect(Collectors.toList());


        if (events.isEmpty()) {
            return List.of();
        }

        return events;
    }

    @Override
    public EventFullDto getEventsById(Integer userId, Integer eventId) {
        userService.getUserById(userId);

        Event event = getEvent(eventId);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivate(Integer userId, Integer eventId,
                                           UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null &&
                updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата события не может быть раньше, чем через 2 часа");
        }

        userService.getUserById(userId);

        Event event = getEvent(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя обновить событие в статусе PUBLISHED");
        }

        if (updateEventUserRequest.getAnnotation() != null && !updateEventUserRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoriesService.getCategoryById(updateEventUserRequest
                    .getCategory())));
        }

        if (updateEventUserRequest.getDescription() != null && !updateEventUserRequest.getDescription().isBlank()) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(locationRepository.findByLatAndLon(updateEventUserRequest.getLocation().getLat(),
                            updateEventUserRequest.getLocation().getLon())
                    .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(updateEventUserRequest
                            .getLocation()))));
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getTitle() != null && !updateEventUserRequest.getTitle().isBlank()) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        eventsRepository.save(event);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto addEventPrivate(Integer userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата события не может быть раньше, чем через 2 часа");
        }

        User user = userService.getUserById(userId);

        Category category = CategoryMapper.toCategory(categoriesService.getCategoryById(newEventDto.getCategory()));

        Location location = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(),
                        newEventDto.getLocation().getLon())
                .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation())));

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        Event event1 = eventsRepository.save(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event1);
        eventFullDto.setConfirmedRequests(0);
        eventFullDto.setViews(0);

        return eventFullDto;
    }

    @Override
    public List<EventFullDto> getEventsAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                             Integer size) {
        GetAdminEvent getAdminEvent = new GetAdminEvent(users, states, categories, rangeStart, rangeEnd);
        BooleanExpression conditions = makeAdminEventQueryFilters(getAdminEvent);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        Page<Event> events = eventsRepository.findAll(conditions, page);

        return events
                .stream()
                .map(this::setViewsAndConfirmedRequestsToEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByIdAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Дата события не может быть раньше, чем через 1 час");
        }

        Event event = getEvent(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя обновить событие в статусе PUBLISHED");
        }

        if (updateEventAdminRequest.getAnnotation() != null && !updateEventAdminRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoriesService.getCategoryById(updateEventAdminRequest
                    .getCategory())));
        }

        if (updateEventAdminRequest.getDescription() != null && !updateEventAdminRequest.getDescription().isBlank()) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationRepository.findByLatAndLon(updateEventAdminRequest.getLocation().getLat(),
                            updateEventAdminRequest.getLocation().getLon())
                    .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(updateEventAdminRequest
                            .getLocation()))));
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Статус долженн быть в ожидании");
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateEventAdminRequest.getTitle() != null && !updateEventAdminRequest.getTitle().isBlank()) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        eventsRepository.save(event);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Integer> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               SortState sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new BadRequestException("Дата окончания не может быть раньше даты начала события");
            }
        }

        GetUserEvent getUserEvent = new GetUserEvent(text, categories, paid, rangeStart, rangeEnd);
        BooleanExpression conditions = makeUserEventQueryFilters(getUserEvent);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        statsService.addHit(request);

        List<Event> events = eventsRepository.findAll(conditions, page).toList();
        List<EventShortDto> eventShortDto = new ArrayList<>();

        if (!events.isEmpty()) {
            if (onlyAvailable) {
                eventShortDto = events
                        .stream()
                        .filter(event -> (event.getParticipantLimit() == 0 ||
                                requestRepository.findAllByEventIdAndStatusEquals(event.getId(),
                                        RequestStatus.CONFIRMED).size()
                                        < event.getParticipantLimit()))
                        .map(this::setViewsAndConfirmedRequestsToEventsShortDto)
                        .collect(Collectors.toList());
            } else {
                eventShortDto = events
                        .stream()
                        .map(this::setViewsAndConfirmedRequestsToEventsShortDto)
                        .collect(Collectors.toList());
            }

            if (sort.equals(SortState.VIEWS)) {
                eventShortDto.sort(Comparator.comparing(EventShortDto::getViews));
            } else {
                eventShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
            }

            return eventShortDto;
        }

        return List.of();
    }

    @Override
    public EventFullDto getEventByIdPublic(Integer id, HttpServletRequest request) {
        Event event = getEvent(id);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ObjectNotFoundException("Событие не опубликовано");
        }

        statsService.addHit(request);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    public Event getEvent(Integer eventId) {

        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Событие не найдено"));
    }

    public static BooleanExpression makeUserEventQueryFilters(GetUserEvent getUserEvent) {
        List<BooleanExpression> conditions = new ArrayList<>();

        if (getUserEvent.getText() != null) {
            String textToSearch = getUserEvent.getText();
            conditions.add(qEvent.title.containsIgnoreCase(textToSearch)
                    .or(qEvent.annotation.containsIgnoreCase(textToSearch))
                    .or(qEvent.description.containsIgnoreCase(textToSearch)));
        }

        if (getUserEvent.getCategories() != null) {
            conditions.add(qEvent.category.id.in(getUserEvent.getCategories()));
        }

        if (getUserEvent.getPaid() != null) {
            conditions.add(qEvent.paid.eq(getUserEvent.getPaid()));
        }

        LocalDateTime rangeStart = getUserEvent.getRangeStart() != null ? getUserEvent.getRangeStart() : LocalDateTime.now();
        conditions.add(qEvent.eventDate.goe(rangeStart));

        if (getUserEvent.getRangeEnd() != null) {
            conditions.add(
                    qEvent.eventDate.loe(getUserEvent.getRangeEnd())
            );
        }

        conditions.add(qEvent.state.eq(EventState.PUBLISHED));

        return conditions
                .stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    public static BooleanExpression makeAdminEventQueryFilters(GetAdminEvent getAdminEvent) {
        List<BooleanExpression> conditions = new ArrayList<>();

        if (getAdminEvent.getCategories() != null) {
            conditions.add(qEvent.category.id.in(getAdminEvent.getCategories()));
        }

        if (getAdminEvent.getStates() != null) {
            conditions.add(qEvent.state.in(getAdminEvent.getStates()));
        }

        if (getAdminEvent.getUsers() != null) {
            conditions.add(qEvent.initiator.id.in(getAdminEvent.getUsers()));
        }
        LocalDateTime rangeStart = getAdminEvent.getRangeStart() != null ? getAdminEvent.getRangeStart() : LocalDateTime.now();
        conditions.add(qEvent.eventDate.goe(rangeStart));

        if (getAdminEvent.getRangeEnd() != null) {
            conditions.add(
                    qEvent.eventDate.loe(getAdminEvent.getRangeEnd())
            );
        }
        return conditions
                .stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private EventShortDto setConfirmedRequestsToEventShortDto(EventShortDto eventShortDto) {
        int request = requestRepository.findAllByEventIdAndStatusEquals(eventShortDto.getId(),
                RequestStatus.CONFIRMED).size();
        eventShortDto.setConfirmedRequests(request);
        return eventShortDto;
    }

    private EventShortDto setViewsAndConfirmedRequestsToEventsShortDto(Event event) {
        LocalDateTime start = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStats> stats = statsService.getStats(start, end, uris, true);

        EventShortDto eventShortDto = EventMapper.toEventShortDto(event);
        if (!stats.isEmpty()) {
            eventShortDto.setViews(Math.toIntExact(stats.get(0).getHits()));
        } else {
            eventShortDto.setViews(0);
        }

        int request = requestRepository.findAllByEventIdAndStatusEquals(event.getId(),
                RequestStatus.CONFIRMED).size();

        eventShortDto.setConfirmedRequests(request);

        return eventShortDto;
    }

    private EventFullDto setViewsAndConfirmedRequestsToEventFullDto(Event event) {
        LocalDateTime start = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStats> stats = statsService.getStats(start, end, uris, true);

        EventFullDto eventFull = EventMapper.toEventFullDto(event);
        if (!stats.isEmpty()) {
            eventFull.setViews(Math.toIntExact(stats.get(0).getHits()));
        } else {
            eventFull.setViews(0);
        }

        int request = requestRepository.findAllByEventIdAndStatusEquals(event.getId(),
                RequestStatus.CONFIRMED).size();

        eventFull.setConfirmedRequests(request);

        return eventFull;
    }
}
