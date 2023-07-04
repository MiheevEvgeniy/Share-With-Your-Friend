package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl service;

    private final UserDto USER_DTO = UserDto.builder()
            .id(1L)
            .name("Sam")
            .email("sam@gmail.com")
            .build();

    @Test
    void getAllUsers_whenInvoked_thenUserListReturned() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");
        List<User> userList = List.of(user, user, user);
        when(repository.findAll()).thenReturn(userList);
        List<UserDto> expectedList = userList
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        List<UserDto> actualList = service.getAllUsers();

        verify(repository).findAll();
        assertEquals(expectedList, actualList);
    }

    @Test
    void getUser_whenInvoked_thenUserReturned() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");
        when(repository.getById(userId)).thenReturn(user);
        UserDto expectedUser = mapper.toDto(user);

        UserDto actualUser = service.getUser(userId);

        verify(repository).getById(userId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void addUser_whenInvoked_thenUserSaved() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");
        when(repository.save(user)).thenReturn(user);
        when(mapper.toEntity(USER_DTO)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(USER_DTO);

        UserDto actualUser = service.addUser(USER_DTO);

        verify(repository).save(user);
        assertEquals(USER_DTO, actualUser);
    }

    @Test
    void deleteUser_whenInvoked_thenUserDeleted() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");
        service.deleteUser(userId);

        verify(repository).deleteById(userId);
    }

    @Test
    void patchUser_whenInvoked_thenUserPatched() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");
        when(repository.save(user)).thenReturn(user);
        when(repository.getById(userId)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(USER_DTO);

        UserDto actualUser = service.patchUser(userId, USER_DTO);

        verify(repository).getById(userId);
        verify(repository).save(user);
        assertEquals(USER_DTO, actualUser);
    }
}
