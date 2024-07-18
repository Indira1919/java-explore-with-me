package ru.practicum.events.model.dto;

import lombok.*;
import ru.practicum.events.model.enums.RequestStatusAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {

    @NotEmpty
    private List<Integer> requestIds;

    @NotNull
    private RequestStatusAction status;
}
