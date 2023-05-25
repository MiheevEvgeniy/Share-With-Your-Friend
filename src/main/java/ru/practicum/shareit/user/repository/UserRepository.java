package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User getUser(Long id);

    void deleteUser(Long id);

    List<User> getAllUsers();

    User addUser(User user);

    User patchUser(User user);
}
