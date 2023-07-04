package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService service;
    private final BookingOutputDto bookingDto = BookingOutputDto.builder()
            .id(1L)
            .booker(new User())
            .item(new Item())
            .status(BookingStatus.WAITING)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .build();
    private final BookingInputDto bookingInputDto = BookingInputDto.builder()
            .itemId(0L)
            .start(LocalDateTime.MAX)
            .end(LocalDateTime.MAX)
            .build();


    @Test
    @SneakyThrows
    void addBooking_whenInvoked_savedBooking() {
        when(service.addBooking(0L, bookingInputDto))
                .thenReturn(bookingDto);

        String response = mvc.perform(post("/bookings", anyLong())
                        .content(mapper.writeValueAsString(bookingInputDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).addBooking(0L, bookingInputDto);
        assertEquals(mapper.writeValueAsString(bookingDto), response);
    }

    @Test
    @SneakyThrows
    void getAllBookingByBooker_whenInvoked_thenReturnedBookerList() {
        List<BookingOutputDto> expectedBookingDtoList = Arrays.asList(bookingDto, bookingDto, bookingDto);
        when(service.getAllBookingsByBookerAndState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(expectedBookingDtoList);

        String response = mvc.perform(get("/bookings", any(), anyInt(), anyInt())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getAllBookingsByBookerAndState(anyLong(), any(), anyInt(), anyInt());
        assertEquals(mapper.writeValueAsString(expectedBookingDtoList), response);
    }

    @Test
    @SneakyThrows
    void getAllBookingByOwner_whenInvoked_thenReturnedBookerList() {
        List<BookingOutputDto> expectedBookingDtoList = Arrays.asList(bookingDto, bookingDto, bookingDto);
        when(service.getAllBookingsByOwnerAndState(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(expectedBookingDtoList);

        String response = mvc.perform(get("/bookings/owner", any(), anyInt(), anyInt())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", anyLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(service).getAllBookingsByOwnerAndState(anyLong(), any(), anyInt(), anyInt());
        assertEquals(mapper.writeValueAsString(expectedBookingDtoList), response);
    }

    @Test
    @SneakyThrows
    void getBooking_whenInvoked_thenReturnedBooking() {
        when(service.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        String response = mvc.perform(get("/bookings/{bookingId}", anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).getBookingById(anyLong(), anyLong());
        assertEquals(mapper.writeValueAsString(bookingDto), response);
    }

    @Test
    @SneakyThrows
    void approveBooking_whenInvoked_thenApproveBooking() {
        when(service.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        String response = mvc.perform(patch("/bookings/{bookingId}", anyLong(), anyBoolean())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(service).approveBooking(anyLong(), anyBoolean(), anyLong());
        assertEquals(mapper.writeValueAsString(bookingDto), response);
    }
}
