package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.mapper.CompilationMapper;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.model.dto.CompilationDto;
import ru.practicum.compilations.model.dto.CompilationShortDto;
import ru.practicum.compilations.model.dto.NewCompilationDto;
import ru.practicum.compilations.model.dto.UpdateCompilationRequest;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.dto.EventShortDto;
import ru.practicum.events.repository.EventsRepository;
import ru.practicum.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationsServiceImpl implements CompilationsService {

    private final CompilationRepository compilationRepository;

    private final EventsRepository eventsRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations;
        List<CompilationDto> compilationsDto = new ArrayList<>();

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }

        List<Integer> comp = new ArrayList<>();
        for (Compilation compilation : compilations) {
            comp.add(compilation.getId());
        }

        Map<Integer, List<Event>> compilationsShortDto = compilationRepository.findAllByIdIn(comp)
                .stream()
                .collect(Collectors.toMap(CompilationShortDto::getId, CompilationShortDto::getEvents));

        for (Compilation compilation : compilations) {
            List<EventShortDto> eventsShortDto = compilationsShortDto.get(compilation.getId())
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());

            compilationsDto.add(CompilationMapper.toCompilationDto(compilation, eventsShortDto));
        }

        return compilationsDto;
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Подборка не найдена"));

        List<EventShortDto> eventsShortDto = compilation.getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilationById(Integer compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Подборка не найдена"));

        List<Event> events = new ArrayList<>();

        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            events = eventsRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        compilationRepository.save(compilation);

        List<EventShortDto> eventsShortDto = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventsRepository.findAllByIdIn(newCompilationDto.getEvents());
        }

        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));

        List<EventShortDto> eventsShortDto = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Integer compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Подборка не найдена"));

        compilationRepository.deleteById(compId);
    }
}
