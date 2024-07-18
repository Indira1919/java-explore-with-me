package ru.practicum.users.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.users.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByIdIn(List<Integer> ids, PageRequest pageRequest);
}
