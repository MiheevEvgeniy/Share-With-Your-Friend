package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserMapperTest {
    @InjectMocks
    private UserMapper mapper;

    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Sam")
            .email("sam@gmail.com")
            .build();

    @Test
    void toDto_whenInvoked_thenUserCastedToUserDto() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");

        UserDto actualDto = mapper.toDto(user);

        assertEquals(userDto, actualDto);
    }

    @Test
    void toEntity_whenInvoked_thenUserDtoCastedToUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");

        User actualUser = mapper.toEntity(userDto);

        assertEquals(user, actualUser);
    }

    @Test
    void toEntity_whenDtoIsNull_thenNullReturned() {
        User actualUser = mapper.toEntity(null);

        assertNull(actualUser);
    }

}
