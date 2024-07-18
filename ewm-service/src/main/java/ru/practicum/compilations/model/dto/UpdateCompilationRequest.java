package ru.practicum.compilations.model.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {

    @Size(max = 50)
    private String title;

    private Boolean pinned;

    private List<Integer> events;
}
