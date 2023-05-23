package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Map<Long, User> users = new HashMap<>();

    private static Long generatedId = 1L;

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(UserDto userDto) {
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .id(generatedId)
                .build();
        if (users.values()
                .stream()
                .anyMatch(user1 ->
                        user1.getEmail().equals(user.getEmail()))) {
            throw new EmailConflictException("Данный email уже существует");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException("Email не введен");
        }
        users.put(generatedId, user);
        generatedId++;
        return user;
    }

    @Override
    public User patchUser(Long userId, User patch) {
        User user = getUser(userId);
        Predicate<User> userPredicate = ((Predicate<User>) user1 -> user1.getEmail().equals(patch.getEmail()))
                .and(user1 -> !user1.getId().equals(userId));

        if (patch.getEmail() != null) {
            if (users.values()
                    .stream()
                    .anyMatch(userPredicate)) {
                throw new EmailConflictException("Данный email уже существует");
            }
            user.setEmail(patch.getEmail());
        }
        if (patch.getName() != null) {
            user.setName(patch.getName());
        }
        users.put(userId, user);
        return user;
    }
}
