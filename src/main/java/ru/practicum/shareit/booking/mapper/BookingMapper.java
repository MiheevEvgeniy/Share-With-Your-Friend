package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingOutputDto toOutputDtoFromEntity(Booking booking) {
        return BookingOutputDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(userRepository.findById(booking.getBookerId()).get())
                .id(booking.getId())
                .item(itemRepository.findById(booking.getItemId()).get())
                .status(booking.getStatus())
                .build();
    }

    public BookingInputDto toInputDtoFromEntity(Booking booking) {
        return BookingInputDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBookerId())
                .id(booking.getId())
                .itemId(booking.getItemId())
                .status(booking.getStatus())
                .build();
    }

    public BookingOutputDto toOutputDtoFromInputDto(BookingInputDto bookingInputDto) {
        return BookingOutputDto.builder()
                .start(bookingInputDto.getStart())
                .end(bookingInputDto.getEnd())
                .booker(userRepository.getById(bookingInputDto.getBookerId()))
                .id(bookingInputDto.getId())
                .item(itemRepository.getById(bookingInputDto.getItemId()))
                .status(bookingInputDto.getStatus())
                .build();
    }

    public Booking toEntityFromInputDto(BookingInputDto bookingInputDto) {
        Booking booking = new Booking();
        booking.setId(bookingInputDto.getId());
        booking.setEnd(bookingInputDto.getEnd());
        booking.setStatus(bookingInputDto.getStatus());
        booking.setItemId(bookingInputDto.getItemId());
        booking.setBookerId(bookingInputDto.getBookerId());
        booking.setStart(bookingInputDto.getStart());
        return booking;
    }

    public Booking toEntityFromOutputDto(BookingOutputDto bookingOutputDto) {
        Booking booking = new Booking();
        booking.setId(bookingOutputDto.getId());
        booking.setEnd(bookingOutputDto.getEnd());
        booking.setStatus(bookingOutputDto.getStatus());
        booking.setItemId(bookingOutputDto.getItem().getId());
        booking.setBookerId(bookingOutputDto.getBooker().getId());
        booking.setStart(bookingOutputDto.getStart());
        return booking;
    }
}
