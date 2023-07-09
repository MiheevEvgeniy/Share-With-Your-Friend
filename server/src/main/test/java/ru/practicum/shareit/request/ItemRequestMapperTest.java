package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestMapperTest {
    @InjectMocks
    private ItemRequestMapper mapper;
    private static User user = new User();
    private static ItemRequest itemRequest = new ItemRequest();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .created(LocalDateTime.MIN)
            .description("description1")
            .build();

    @BeforeAll
    static void createData() {
        long userId = 1L;
        user = new User();
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");

        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setCreated(LocalDateTime.MIN);
        itemRequest.setDescription("description1");
        itemRequest.setOwner(user);
    }

    @Test
    void toDto_whenInvoked_thenItemRequestCastedToDto() {
        ItemRequestDto actualDto = mapper.toDto(itemRequest);

        assertEquals(itemRequestDto, actualDto);
    }

    @Test
    void toEntity_whenInvoked_thenDtoCastedToItemRequest() {
        ItemRequest actualRequest = mapper.toEntity(itemRequestDto, user);

        assertEquals(itemRequest, actualRequest);
    }
}
