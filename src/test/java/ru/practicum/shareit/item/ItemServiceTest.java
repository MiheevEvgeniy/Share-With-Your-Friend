package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.OwnerAccessException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentMapper;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {
    @Mock
    private ItemRepository repository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper mapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl service;

    private final ItemDto ITEM_DTO = ItemDto.builder()
            .id(1L)
            .name("item1")
            .description("description1")
            .available(true)
            .requestId(2L)
            .build();
    private static Item item = new Item();
    private static User user = new User();

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
    }

    @Test
    void getAllItemsByOwner_whenInvoked_thenItemListReturned() {

        List<Item> itemList = List.of(item, item, item);
        when(repository.findAll()).thenReturn(itemList);
        when(mapper.toDto(any(), anyLong(), any(), any(), anyList())).thenReturn(ITEM_DTO);
        List<ItemDto> expectedList = List.of(ITEM_DTO, ITEM_DTO, ITEM_DTO);

        List<ItemDto> actualList = service.getAllItemsByOwner(user.getId());

        verify(repository).findAll();
        assertEquals(expectedList, actualList);
    }

    @Test
    void getItem_whenInvoked_thenItemReturned() {
        when(repository.getById(item.getId())).thenReturn(item);
        when(mapper.toDto(any(), anyLong(), any(), any(), anyList())).thenReturn(ITEM_DTO);
        ItemDto expectedItem = ITEM_DTO;

        ItemDto actualItem = service.getItem(user.getId(), item.getId());

        verify(repository).getById(item.getId());
        assertEquals(expectedItem, actualItem);
    }

    @Test
    void searchItem_whenInvoked_thenResultReturned() {
        List<Item> list = List.of(item, item, item);
        when(repository.findAll()).thenReturn(list);
        when(mapper.toDto(any(), anyLong(), any(), any(), anyList())).thenReturn(ITEM_DTO);

        List<ItemDto> expectedList = List.of(ITEM_DTO, ITEM_DTO, ITEM_DTO);
        List<ItemDto> actualList = service.searchItem("item");

        verify(repository).findAll();
        assertEquals(expectedList, actualList);
    }

    @Test
    void searchItem_whenTextIsBlank_thenEmptyListReturned() {
        List<ItemDto> actualListBlank = service.searchItem("");

        verify(repository, never()).findAll();
        assertEquals(new ArrayList<>(), actualListBlank);
    }

    @Test
    void addItem_whenInvoked_thenItemSaved() {
        when(repository.save(item)).thenReturn(item);
        when(mapper.toDto(any(), anyLong(), any(), any(), anyList())).thenReturn(ITEM_DTO);
        when(mapper.toEntity(any(), any(), any())).thenReturn(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        ItemDto expectedItem = ITEM_DTO;

        ItemDto actualItem = service.addItem(ITEM_DTO, user.getId());

        verify(repository).save(item);
        assertEquals(expectedItem, actualItem);
    }

    @Test
    void deleteItem_whenInvoked_thenItemDeleted() {
        when(repository.getById(item.getId())).thenReturn(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.deleteItem(item.getId(), user.getId());

        verify(repository).deleteById(item.getId());
    }

    @Test
    void deleteItem_whenUserIsNotOwner_thenThrowOwnerAccessException() {
        long wrongOwnerId = 2L;
        when(repository.getById(item.getId())).thenReturn(item);
        when(userRepository.findById(wrongOwnerId)).thenReturn(Optional.of(user));

        OwnerAccessException exception = assertThrows(OwnerAccessException.class, () -> service.deleteItem(item.getId(), wrongOwnerId));
        verify(repository, never()).deleteById(item.getId());
    }

    @Test
    void patchItem_whenInvoked_thenItemPatched() {
        when(repository.getById(item.getId())).thenReturn(item);
        when(mapper.toDto(any(), anyLong(), any(), any(), anyList())).thenReturn(ITEM_DTO);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        ItemDto actualItem = service.patchItem(item.getId(), ITEM_DTO, user.getId());

        verify(repository, times(2)).getById(user.getId());
        verify(repository).save(item);
        assertEquals(ITEM_DTO, actualItem);
    }

    @Test
    void patchItem_whenUserIsNotOwner_thenThrowOwnerAccessException() {
        long wrongOwnerId = 2L;
        when(repository.getById(item.getId())).thenReturn(item);
        when(userRepository.findById(wrongOwnerId)).thenReturn(Optional.of(user));

        OwnerAccessException exception = assertThrows(OwnerAccessException.class, () -> service.patchItem(item.getId(), ITEM_DTO, wrongOwnerId));
        verify(repository).getById(user.getId());
        verify(repository, never()).save(item);
    }

    @Test
    void addComment_whenInvoked_thenCommentAdded() {
        long commentId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .itemId(1L)
                .text("comment text")
                .created(LocalDateTime.now())
                .id(commentId)
                .authorName("Sam")
                .build();
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText("comment text");
        comment.setCreated(LocalDateTime.now());
        comment.setId(commentId);

        long bookingId = 1L;
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.MIN);
        booking.setId(bookingId);

        when(repository.save(item)).thenReturn(item);
        when(mapper.toDto(any(), anyLong(), any(), any(), anyList())).thenReturn(ITEM_DTO);
        when(mapper.toEntity(any(), any(), any())).thenReturn(item);
        when(commentMapper.toDto(any())).thenReturn(commentDto);
        when(commentMapper.toEntity(any(), any(), any())).thenReturn(comment);
        when(userRepository.getById(user.getId())).thenReturn(user);
        when(repository.getById(item.getId())).thenReturn(item);
        when(bookingRepository.findAll()).thenReturn(List.of(booking));
        CommentDto expectedCommentDto = commentDto;

        CommentDto actualCommentDto = service.addComment(item.getId(), user.getId(), commentDto);

        verify(commentRepository).save(comment);
        assertEquals(expectedCommentDto, actualCommentDto);
    }
}
