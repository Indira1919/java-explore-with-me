package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.model.dto.CompilationDto;
import ru.practicum.compilations.model.dto.NewCompilationDto;
import ru.practicum.compilations.model.dto.UpdateCompilationRequest;
import ru.practicum.compilations.service.CompilationsService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {

    private final CompilationsService compilationsService;

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationById(@PathVariable Integer compId,
                                                @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationsService.updateCompilationById(compId, updateCompilationRequest);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationsService.addCompilation(newCompilationDto);
    }


    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Integer compId) {
        compilationsService.deleteCompilationById(compId);
    }

}
