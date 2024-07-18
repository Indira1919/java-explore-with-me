package ru.practicum.users.service;

import ru.practicum.users.model.dto.NewUserRequest;
import ru.practicum.users.model.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUserById(Integer userId);
}
