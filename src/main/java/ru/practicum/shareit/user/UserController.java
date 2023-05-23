package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Обработка запроса getAllUsers начата");
        List<UserDto> users = service.getAllUsers();
        log.info("Результат запроса getAllUsers: {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("id для запроса getUser: {}", id);
        UserDto userDto = service.getUser(id);
        log.info("Результат запроса getUser: {}", userDto);
        return userDto;
    }

    @PostMapping()
    public UserDto addUser(@Valid @RequestBody UserDto user) {
        log.info("user для запроса addUser: {}", user);
        UserDto userDto = service.addUser(user);
        log.info("Результат запроса addUser: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Удаление пользователя с id {} начато", id);
        service.deleteUser(id);
        log.info("Удаление пользователя с id {} завершено", id);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable long id, @RequestBody User user) {
        log.info("patchUser: id {}, user {} патч начат", id, user);
        UserDto userDto = service.patchUser(id, user);
        log.info("Патч завершен. Результат: {}", userDto);
        return userDto;
    }
}
