package ru.practicum.compilations.service;

import ru.practicum.compilations.model.dto.CompilationDto;
import ru.practicum.compilations.model.dto.NewCompilationDto;
import ru.practicum.compilations.model.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationsService {

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Integer compId);

    CompilationDto updateCompilationById(Integer compId, UpdateCompilationRequest updateCompilationRequest);

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Integer compId);
}
