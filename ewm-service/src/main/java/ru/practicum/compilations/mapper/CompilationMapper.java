package ru.practicum.compilations.mapper;

import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.model.dto.CompilationDto;
import ru.practicum.compilations.model.dto.NewCompilationDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.dto.EventShortDto;

import java.util.List;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation
                .builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return CompilationDto
                .builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(events)
                .build();
    }
}
