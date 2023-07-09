package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingMapperTest {
    @InjectMocks
    private BookingMapper mapper;

    private static Item item = new Item();
    private static User user = new User();
    private static Booking booking = new Booking();
    private final BookingOutputDto bookingOutputDto = BookingOutputDto.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .status(BookingStatus.WAITING)
            .end(LocalDateTime.now())
            .start(LocalDateTime.now())
            .build();
    private final BookingInputDto bookingInputDto = BookingInputDto.builder()
            .itemId(1L)
            .end(LocalDateTime.now())
            .start(LocalDateTime.MIN)
            .build();

    @BeforeAll
    static void createData() {
        long userId = 1L;
        user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");

        long itemId = 1L;
        item = new Item();
        item.setName("item2");
        item.setDescription("description2");
        item.setAvailable(true);
        item.setId(itemId);
        item.setOwner(user);

        long bookingId = 1L;
        booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.MIN);
        booking.setId(bookingId);
    }

    @Test
    void toOutputDtoFromEntity_whenInvoked_thenEntityCastedToOutputDto() {

        BookingOutputDto actualDto = mapper.toOutputDtoFromEntity(booking);

        assertEquals(bookingOutputDto, actualDto);
    }

    @Test
    void toEntityFromInputDto_whenInvoked_thenInputDtoCastedToEntity() {

        Booking actualBooking = mapper.toEntityFromInputDto(bookingInputDto, item, user, BookingStatus.WAITING);
        actualBooking.setId(booking.getId());
        assertEquals(booking, actualBooking);
    }
}
