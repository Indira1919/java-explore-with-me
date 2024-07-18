package ru.practicum.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.events.model.Request;
import ru.practicum.events.model.dto.ConfirmedRequests;
import ru.practicum.events.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByRequesterId(Integer requesterId);

    Optional<Request> findByEventIdAndRequesterId(Integer eventId, Integer userId);

    List<Request> findAllByEventIdAndStatusEquals(Integer eventId, RequestStatus status);

    List<Request> findAllByEventId(Integer eventId);

    List<Request> findAllByIdIn(List<Integer> requestIds);

    @Query("SELECT new ru.practicum.events.model.dto.ConfirmedRequests(r.event.id, count(r.id)) " +
                  "FROM Request AS r " +
                  "WHERE r.event.id IN ?1 " +
                  "AND r.status = 'CONFIRMED' " +
                  "GROUP BY r.event.id")
    List<ConfirmedRequests> getConfirmedRequests(List<Integer> eventsId);
}
