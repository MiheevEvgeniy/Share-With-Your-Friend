package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return mapper.toDto(repository.getById(id));
    }

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        return mapper.toDto(repository.save(mapper.toEntity(userDto)));
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    @Override
    public UserDto patchUser(long id, UserDto patch) {
        User user = repository.getById(id);
        if (patch.getName() != null) {
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null) {
            user.setEmail(patch.getEmail());
        }
        return mapper.toDto(repository.save(user));
    }
}
