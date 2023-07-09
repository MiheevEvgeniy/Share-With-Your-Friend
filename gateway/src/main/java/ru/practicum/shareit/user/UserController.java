package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Обработка запроса getAllUsers начата");
        ResponseEntity<Object> users = client.getAllUsers();
        log.info("Результат запроса getAllUsers: {}", users);
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("id для запроса getUser: {}", id);
        ResponseEntity<Object> userDto = client.getUser(id);
        log.info("Результат запроса getUser: {}", userDto);
        return userDto;
    }

    @PostMapping()
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto user) {
        log.info("user для запроса addUser: {}", user);
        ResponseEntity<Object> userDto = client.addUser(user);
        log.info("Результат запроса addUser: {}", userDto);
        return userDto;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Удаление пользователя с id {} начато", id);
        client.deleteUser(id);
        log.info("Удаление пользователя с id {} завершено", id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(@PathVariable long id, @RequestBody UserDto user) {
        log.info("patchUser: id {}, user {} патч начат", id, user);
        ResponseEntity<Object> userDto = client.patchUser(id, user);
        log.info("Патч завершен. Результат: {}", userDto);
        return userDto;
    }
}
