package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService service;
    private final UserDto USER_DTO = UserDto.builder()
            .id(1L)
            .name("Sam")
            .email("sam@gmail.com")
            .build();

    @Test
    @SneakyThrows
    void addUser_whenInvoked_savedUser() {
        when(service.addUser(any()))
                .thenReturn(USER_DTO);

        String response = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(USER_DTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).addUser(any());
        assertEquals(mapper.writeValueAsString(USER_DTO), response);
    }

    @Test
    @SneakyThrows
    void addUser_whenNotValid_thenReturnedBadRequest() {

        UserDto userInputDto = UserDto.builder()
                .email(null)
                .build();
        when(service.addUser(userInputDto))
                .thenReturn(USER_DTO);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).addUser(userInputDto);
    }

    @Test
    @SneakyThrows
    void getAllUsers_whenInvoked_thenReturnedUserList() {
        List<UserDto> expectedUserDtoList = Arrays.asList(USER_DTO, USER_DTO, USER_DTO);
        when(service.getAllUsers())
                .thenReturn(expectedUserDtoList);

        String response = mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(expectedUserDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getAllUsers();
        assertEquals(mapper.writeValueAsString(expectedUserDtoList), response);
    }

    @Test
    @SneakyThrows
    void getUser_whenInvoked_thenReturnedUser() {
        when(service.getUser(anyLong()))
                .thenReturn(USER_DTO);

        String response = mvc.perform(get("/users/{userId}", anyLong())
                        .content(mapper.writeValueAsString(USER_DTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getUser(anyLong());
        assertEquals(mapper.writeValueAsString(USER_DTO), response);
    }

    @Test
    @SneakyThrows
    void deleteUser_whenInvoked_deletedUser() {
        mvc.perform(delete("/users/{userId}", anyLong())
                        .content(mapper.writeValueAsString(USER_DTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).deleteUser(anyLong());
    }

    @Test
    @SneakyThrows
    void patchUser_whenInvoked_patchedUser() {
        when(service.patchUser(anyLong(), any()))
                .thenReturn(USER_DTO);

        String response = mvc.perform(patch("/users/{userId}", anyLong(), any())
                        .content(mapper.writeValueAsString(USER_DTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).patchUser(anyLong(), any());
        assertEquals(mapper.writeValueAsString(USER_DTO), response);
    }
}
