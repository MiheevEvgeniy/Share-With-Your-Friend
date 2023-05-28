package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.function.Predicate;
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
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(long id) {
        return mapper.toDto(repository.getUser(id));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (getAllUsers()
                .stream()
                .anyMatch(user1 ->
                        user1.getEmail().equals(userDto.getEmail()))) {
            throw new EmailConflictException("Данный email уже существует");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new InvalidEmailException("Email не введен");
        }
        return mapper.toDto(repository.addUser(mapper.toEntity(userDto)));
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteUser(id);
    }

    @Override
    public UserDto patchUser(long id, UserDto patch) {
        patch.setId(id);
        User user = repository.getUser(id);
        Predicate<UserDto> userPredicate = ((Predicate<UserDto>) user1 -> user1.getEmail().equals(patch.getEmail()))
                .and(user1 -> !user1.getId().equals(id));
        if (patch.getEmail() != null) {
            if (getAllUsers()
                    .stream()
                    .anyMatch(userPredicate)) {
                throw new EmailConflictException("Данный email уже существует");
            }
            user.setEmail(patch.getEmail());
        }
        if (patch.getName() != null) {
            user.setName(patch.getName());
        }
        return mapper.toDto(repository.patchUser(user));
    }
}
