package ru.practicum.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comments.model.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByEventId(Integer eventId, PageRequest page);

    List<Comment> findAllByUserIdAndEventId(Integer userId, Integer eventId, PageRequest page);

    List<Comment> findAllByUserId(Integer userId, PageRequest page);
}
