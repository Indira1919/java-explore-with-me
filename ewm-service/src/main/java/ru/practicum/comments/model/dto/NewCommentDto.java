package ru.practicum.comments.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCommentDto {

    @NotBlank
    @Size(min = 10, max = 2000)
    private String text;
}
