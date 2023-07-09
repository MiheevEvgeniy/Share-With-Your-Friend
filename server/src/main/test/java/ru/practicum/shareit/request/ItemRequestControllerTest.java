package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemRequestService service;
    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.now())
            .items(Collections.emptyList())
            .build();

    @Test
    @SneakyThrows
    void addRequest_whenInvoked_savedRequest() {
        when(service.addRequest(any(), anyLong()))
                .thenReturn(requestDto);

        String response = mvc.perform(post("/requests", anyLong())
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).addRequest(any(), anyLong());
        assertEquals(mapper.writeValueAsString(requestDto), response);
    }

    @Test
    @SneakyThrows
    void getAllRequests_whenInvoked_thenReturnedRequestList() {
        List<ItemRequestDto> expectedRequestDtoList = Arrays.asList(requestDto, requestDto, requestDto);
        when(service.getAllRequests(anyLong()))
                .thenReturn(expectedRequestDtoList);

        String response = mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getAllRequests(anyLong());
        assertEquals(mapper.writeValueAsString(expectedRequestDtoList), response);
    }

    @Test
    @SneakyThrows
    void getAllRequestsPageable_whenInvoked_thenReturnedPageableRequestList() {
        List<ItemRequestDto> expectedRequestDtoList = Arrays.asList(requestDto, requestDto, requestDto);
        when(service.getAllRequestsPageable(anyLong(), anyInt(), anyInt()))
                .thenReturn(expectedRequestDtoList);

        String response = mvc.perform(get("/requests/all", anyInt(), anyInt())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getAllRequestsPageable(anyLong(), anyInt(), anyInt());
        assertEquals(mapper.writeValueAsString(expectedRequestDtoList), response);
    }

    @Test
    @SneakyThrows
    void getRequest_whenInvoked_thenReturnedRequest() {
        when(service.getRequest(anyLong(), anyLong()))
                .thenReturn(requestDto);

        String response = mvc.perform(get("/requests/{requestId}", anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getRequest(anyLong(), anyLong());
        assertEquals(mapper.writeValueAsString(requestDto), response);
    }

}
