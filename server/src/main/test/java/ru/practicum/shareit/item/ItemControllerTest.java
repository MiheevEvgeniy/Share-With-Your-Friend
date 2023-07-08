package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService service;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Item")
            .description("Description")
            .available(true)
            .build();
    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .authorName("Max")
            .created(LocalDateTime.now())
            .text("text")
            .build();

    @Test
    @SneakyThrows
    void addItem_whenInvoked_savedItem() {
        when(service.addItem(any(), anyLong()))
                .thenReturn(itemDto);

        String response = mvc.perform(post("/items", anyLong())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).addItem(any(), anyLong());
        assertEquals(mapper.writeValueAsString(itemDto), response);
    }

    @Test
    @SneakyThrows
    void getAllItemsByOwner_whenInvoked_thenReturnedItemList() {
        List<ItemDto> expectedItemDtoList = Arrays.asList(itemDto, itemDto, itemDto);
        when(service.getAllItemsByOwner(anyLong()))
                .thenReturn(expectedItemDtoList);

        String response = mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getAllItemsByOwner(anyLong());
        assertEquals(mapper.writeValueAsString(expectedItemDtoList), response);
    }

    @Test
    @SneakyThrows
    void getItem_whenInvoked_thenReturnedItem() {
        when(service.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);

        String response = mvc.perform(get("/items/{itemId}", anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getItem(anyLong(), anyLong());
        assertEquals(mapper.writeValueAsString(itemDto), response);
    }

    @Test
    @SneakyThrows
    void deleteItem_whenInvoked_deletedItem() {
        mvc.perform(delete("/items/{itemId}", anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).deleteItem(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void patchItem_whenInvoked_patchedItem() {
        when(service.patchItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        String response = mvc.perform(patch("/items/{id}", anyLong(), any())
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).patchItem(anyLong(), any(), anyLong());
        assertEquals(mapper.writeValueAsString(itemDto), response);
    }

    @Test
    @SneakyThrows
    void searchItem_whenInvoked_thenReturnedSearchResult() {
        List<ItemDto> expectedItemDtoList = List.of(itemDto, itemDto, itemDto);
        when(service.searchItem(anyString()))
                .thenReturn(expectedItemDtoList);

        String response = mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", anyString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).searchItem(anyString());
        assertEquals(mapper.writeValueAsString(expectedItemDtoList), response);
    }

    @Test
    @SneakyThrows
    void addComment_whenInvoked_thenCommentAdded() {
        when(service.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        String response = mvc.perform(post("/items/{itemId}/comment", anyLong(), anyLong())
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).addComment(anyLong(), anyLong(), any());
        assertEquals(mapper.writeValueAsString(commentDto), response);
    }
}
