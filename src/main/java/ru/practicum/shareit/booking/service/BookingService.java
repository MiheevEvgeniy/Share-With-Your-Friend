package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingOutputDto addBooking(long bookerId, BookingInputDto bookingInputDto);

    BookingOutputDto approveBooking(long ownerId, Boolean approved, Long bookingId);

    BookingOutputDto getBookingById(long bookerId, Long bookingId);

    List<BookingOutputDto> getAllBookingsByBookerAndState(long bookerId, BookingStatus state);

    List<BookingOutputDto> getAllBookingsByOwnerAndState(long bookerId, BookingStatus state);
}
