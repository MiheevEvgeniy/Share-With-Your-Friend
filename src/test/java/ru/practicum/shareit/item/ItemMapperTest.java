package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemMapperTest {
    @InjectMocks
    private ItemMapper mapper;

    private final ItemDto ITEM_DTO = ItemDto.builder()
            .id(1L)
            .name("item1")
            .description("description1")
            .available(true)
            .requestId(2L)
            .build();
    private static Item item = new Item();
    private static User user = new User();
    private final ItemDtoForRequest ITEM_DTO_FOR_REQUEST = ItemDtoForRequest.builder()
            .id(1L)
            .name("item1")
            .description("description1")
            .available(true)
            .requestId(2L)
            .build();
    private static Booking booking;

    @BeforeAll
    static void createData() {
        long userId = 1L;
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");

        long itemId = 1L;
        item.setName("item2");
        item.setDescription("description2");
        item.setAvailable(true);
        item.setId(itemId);
        item.setOwner(user);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        item.setRequest(itemRequest);

        long bookingId = 1L;
        booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.MIN);
        booking.setId(bookingId);
    }

    @Test
    void toDto_whenInvoked_thenItemCastedToItemDto() {

        ItemDto actualDto = mapper.toDto(item, user.getId(), booking, booking, null);

        assertEquals(ITEM_DTO, actualDto);
    }

    @Test
    void toEntity_whenInvoked_thenItemDtoCastedToItem() {

        Item actualItem = mapper.toEntity(ITEM_DTO, user, null);

        assertEquals(item, actualItem);
    }

    @Test
    void toEntity_whenDtoIsNull_thenNullReturned() {
        Item actualItem = mapper.toEntity(null, null, null);

        assertNull(actualItem);
    }

    @Test
    void toDtoForRequest_whenInvoked_thenItemCastedToItemForRequest() {
        ItemDtoForRequest actualDto = mapper.toDtoForRequest(item);

        assertEquals(ITEM_DTO_FOR_REQUEST, actualDto);
    }
}
