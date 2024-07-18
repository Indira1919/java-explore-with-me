package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.model.dto.NewUserRequest;
import ru.practicum.users.model.dto.UserDto;
import ru.practicum.users.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        if (ids == null || ids.isEmpty()) {

            return userRepository.findAll(page)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {

            return userRepository.findByIdIn(ids, page)
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserNew(newUserRequest)));
    }

    @Transactional
    @Override
    public void deleteUserById(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        userRepository.deleteById(userId);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
    }
}
