package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository repository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper mapper;
    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private static Item item = new Item();
    private static User user = new User();
    private static Booking booking = new Booking();
    private static ItemRequest itemRequest = new ItemRequest();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("description1")
            .build();
    private final ItemDtoForRequest itemDtoForRequest = ItemDtoForRequest.builder()
            .name("item2")
            .description("description2")
            .requestId(1L)
            .available(true)
            .id(1L)
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

        long itemRequestId = 1L;
        itemRequest.setId(itemRequestId);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("description1");
        itemRequest.setOwner(user);
    }

    @Test
    void getAllRequests_whenInvoked_thenRequestListReturned() {
        List<ItemRequest> requestList = List.of(itemRequest, itemRequest, itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toDto(any())).thenReturn(itemRequestDto);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item, item, item));
        when(itemMapper.toDtoForRequest(any())).thenReturn(itemDtoForRequest);
        when(repository.findAllByOwnerOrderByCreatedDesc(any())).thenReturn(requestList);
        List<ItemRequestDto> expectedList = List.of(itemRequestDto, itemRequestDto, itemRequestDto);

        List<ItemRequestDto> actualList = service.getAllRequests(user.getId());

        verify(repository).findAllByOwnerOrderByCreatedDesc(any());
        assertEquals(expectedList, actualList);
    }

    @Test
    void getAllRequestsPageable_whenInvoked_thenPageableRequestListReturned() {
        List<ItemRequest> requestList = List.of(itemRequest, itemRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toDto(any())).thenReturn(itemRequestDto);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item, item));
        when(itemMapper.toDtoForRequest(any())).thenReturn(itemDtoForRequest);
        when(repository.findByRequester_IdNot(user.getId(), PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id")))).thenReturn(requestList);
        List<ItemRequestDto> expectedList = List.of(itemRequestDto, itemRequestDto);

        List<ItemRequestDto> actualList = service.getAllRequestsPageable(user.getId(), 0, 2);

        verify(repository).findByRequester_IdNot(user.getId(), PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id")));
        assertEquals(expectedList, actualList);
    }

    @Test
    void getRequest_whenInvoked_thenRequestReturned() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(mapper.toDto(any())).thenReturn(itemRequestDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item, item, item));
        when(itemMapper.toDtoForRequest(any())).thenReturn(itemDtoForRequest);
        ItemRequestDto expectedRequest = itemRequestDto;

        ItemRequestDto actualRequest = service.getRequest(itemRequest.getId(), user.getId());

        verify(itemRepository).findAllByRequestId(anyLong());
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    void getRequest_whenItemRequestNotFound_thenThrowItemRequestNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemRequestNotFoundException exception = assertThrows(ItemRequestNotFoundException.class,
                () -> service.getRequest(itemRequest.getId(), user.getId()));
        verify(itemRepository, never()).findAllByRequestId(anyLong());
        assertEquals("Запрос не найден", exception.getMessage());
    }

    @Test
    void addRequest_whenInvoked_thenRequestSaved() {
        when(repository.save(any())).thenReturn(itemRequest);
        when(mapper.toEntity(any(), any())).thenReturn(itemRequest);
        when(mapper.toDto(any())).thenReturn(itemRequestDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemRequestDto actualDto = service.addRequest(itemRequestDto, user.getId());

        verify(repository).save(any());
        assertEquals(itemRequestDto, actualDto);
    }

    @Test
    void addRequest_whenUserNotFound_thenThrowOwnerNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class,
                () -> service.addRequest(itemRequestDto, user.getId()));
        verify(repository, never()).save(any());
        assertEquals("Владелец предмета не найден", exception.getMessage());
    }
}
