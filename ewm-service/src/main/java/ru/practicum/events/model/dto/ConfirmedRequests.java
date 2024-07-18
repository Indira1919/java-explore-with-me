package ru.practicum.events.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmedRequests {

    private Integer eventId;

    private Long confirmedRequests;
}
