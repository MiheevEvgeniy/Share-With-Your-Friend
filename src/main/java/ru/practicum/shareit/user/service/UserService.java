package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(long id);

    UserDto addUser(UserDto user);

    void deleteUser(long id);

    UserDto patchUser(long id, UserDto user);
}
