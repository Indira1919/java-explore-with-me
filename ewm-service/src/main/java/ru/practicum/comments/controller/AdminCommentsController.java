package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.model.dto.CommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentsController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getCommentsAdmin(
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsAdmin(from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentsAdmin(@PathVariable Integer commentId) {
        commentService.deleteCommentsAdmin(commentId);
    }
}

