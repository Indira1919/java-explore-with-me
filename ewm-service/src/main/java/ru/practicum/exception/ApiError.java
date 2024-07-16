package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {

    private final String message;

    private final String status;

    private final String reason;

    private final String timestamp;
}
