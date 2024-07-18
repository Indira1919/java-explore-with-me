package ru.practicum.events.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.events.model.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestDto {

    private Integer id;

    private Integer event;

    private Integer requester;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    private RequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationRequestDto that = (ParticipationRequestDto) o;
        return Objects.equals(id, that.id) && Objects.equals(event, that.event) &&
                Objects.equals(requester, that.requester) && Objects.equals(created, that.created) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, event, requester, created, status);
    }
}
