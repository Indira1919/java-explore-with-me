package ru.practicum.compilations.model.dto;

import lombok.*;
import ru.practicum.events.model.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationDto {

    private Integer id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;
}
