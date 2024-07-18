package ru.practicum.compilations.model.dto;

import ru.practicum.events.model.Event;

import java.util.List;

public interface CompilationShortDto {
    Integer getId();

    List<Event> getEvents();
}
