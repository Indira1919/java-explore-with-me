package ru.practicum.events.model.dto;

import lombok.*;
import ru.practicum.events.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAdminEvent {

    private List<Integer> users;

    private List<EventState> states;

    private List<Integer> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;
}
