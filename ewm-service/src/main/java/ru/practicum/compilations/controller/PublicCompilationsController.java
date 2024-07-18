package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.model.dto.CompilationDto;
import ru.practicum.compilations.service.CompilationsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {

    private final CompilationsService compilationsService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @PositiveOrZero @RequestParam (defaultValue = "0") Integer from,
                                                @Positive @RequestParam (defaultValue = "10") Integer size) {
        return compilationsService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Integer compId) {
        return compilationsService.getCompilationById(compId);
    }
}
