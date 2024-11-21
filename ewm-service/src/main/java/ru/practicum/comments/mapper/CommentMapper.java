package ru.practicum.comments.mapper;

import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.dto.CommentDto;
import ru.practicum.comments.model.dto.NewCommentDto;
import ru.practicum.users.model.dto.UserShortDto;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .eventId(comment.getEvent().getId())
                .user(UserShortDto.builder()
                        .id(comment.getUser().getId())
                        .name(comment.getUser().getName())
                        .build())
                .build();
    }

    public static Comment toComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }
}
