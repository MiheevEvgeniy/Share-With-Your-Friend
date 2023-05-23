package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.getAllUsers()
                .stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return mapper.map(repository.getUser(id));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        return mapper.map(repository.addUser(userDto));
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteUser(id);
    }

    @Override
    public UserDto patchUser(long id, User user) {
        return mapper.map(repository.patchUser(id, user));
    }
}
