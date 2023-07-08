package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.BookingStatus;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingOutputDto addBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                       @Valid @RequestBody BookingInputDto bookingInputDto) {
        return service.addBooking(bookerId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                           @RequestParam Boolean approved,
                                           @PathVariable Long bookingId) {
        return service.approveBooking(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingById(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                           @PathVariable Long bookingId) {
        return service.getBookingById(bookerId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> getAllBookingsByBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                         @RequestParam(required = false, defaultValue = "ALL")
                                                         BookingStatus state,
                                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return service.getAllBookingsByBookerAndState(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                        @RequestParam(required = false, defaultValue = "ALL") BookingStatus state,
                                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return service.getAllBookingsByOwnerAndState(bookerId, state, from, size);
    }
}
