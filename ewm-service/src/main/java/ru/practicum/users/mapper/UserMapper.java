package ru.practicum.users.mapper;

import ru.practicum.users.model.dto.NewUserRequest;
import ru.practicum.users.model.User;
import ru.practicum.users.model.dto.UserDto;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto
                .builder()
                .email(user.getEmail())
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUserNew(NewUserRequest userDto) {
        return User
                .builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }
}
