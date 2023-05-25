package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private Long generatedId = 0L;

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
    public User addUser(User user) {
        user.setId(++generatedId);
        users.put(generatedId, user);
        return user;
    }

    @Override
    public User patchUser(User user) {
        users.put(user.getId(), user);
        return user;
    }
}
