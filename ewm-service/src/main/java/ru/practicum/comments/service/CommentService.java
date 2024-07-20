package ru.practicum.comments.service;

import ru.practicum.comments.model.dto.CommentDto;
import ru.practicum.comments.model.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getCommentsAdmin(Integer from, Integer size);

    void deleteCommentsAdmin(Integer commentId);

    List<CommentDto> getComments(Integer userId, Integer eventId, Integer from, Integer size);

    CommentDto updateComments(Integer userId, Integer commentId, NewCommentDto newCommentDto);

    CommentDto addComment(Integer userId, Integer eventId, NewCommentDto newCommentDto);

    void deleteComment(Integer userId, Integer commentId);

    List<CommentDto> getCommentsPublic(Integer eventId, Integer from, Integer size);
}
