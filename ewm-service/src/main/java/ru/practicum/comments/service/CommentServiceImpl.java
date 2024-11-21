package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.dto.CommentDto;
import ru.practicum.comments.model.dto.NewCommentDto;
import ru.practicum.comments.repository.CommentsRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.enums.EventState;
import ru.practicum.events.service.EventsServiceImpl;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentsRepository commentsRepository;
    private final UserServiceImpl userService;
    private final EventsServiceImpl eventsService;

    @Override
    public List<CommentDto> getCommentsAdmin(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return commentsRepository.findAll(page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommentsAdmin(Integer commentId) {
        commentsRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException("Комментарий не найден"));

        commentsRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getComments(Integer userId, Integer eventId, Integer from, Integer size) {
        userService.getUserById(userId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Comment> comments;
        if (eventId != null) {
            eventsService.getEvent(eventId);

            comments = commentsRepository.findAllByUserIdAndEventId(userId, eventId, page);
        } else {
            comments = commentsRepository.findAllByUserId(userId, page);
        }

        if (comments.isEmpty()) {
            return List.of();
        }

        return comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateComments(Integer userId, Integer commentId, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException("Комментарий не найден"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("Вы не можете редактировать данный комментарий");
        }

        comment.setText(newCommentDto.getText());
        comment.setCreatedOn(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentsRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer userId, Integer eventId, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);
        Event event = eventsService.getEvent(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Вы не млжете оставить комментарий на не опубликованное событие");
        }

        Comment comment = CommentMapper.toComment(newCommentDto);
        comment.setUser(user);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentsRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Integer userId, Integer commentId) {
        userService.getUserById(userId);

        Comment comment = commentsRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException("Комментарий не найден"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException("Вы не можете удалить данный комментарий");
        }

        commentsRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsPublic(Integer eventId, Integer from, Integer size) {
        eventsService.getEvent(eventId);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return commentsRepository.findAllByEventId(eventId, page)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
