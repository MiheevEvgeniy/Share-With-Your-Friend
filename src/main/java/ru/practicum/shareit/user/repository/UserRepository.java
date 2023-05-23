package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User getUser(Long id);

    void deleteUser(Long id);

    List<User> getAllUsers();

    User addUser(UserDto userDto);

    User patchUser(Long userId, User updatedUser);
}
