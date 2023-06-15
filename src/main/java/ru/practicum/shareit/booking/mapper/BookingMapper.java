package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public BookingOutputDto toOutputDtoFromEntity(Booking booking) {
        return BookingOutputDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .id(booking.getId())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public Booking toEntityFromInputDto(BookingInputDto bookingInputDto, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        booking.setEnd(bookingInputDto.getEnd());
        booking.setStart(bookingInputDto.getStart());
        return booking;
    }

}
