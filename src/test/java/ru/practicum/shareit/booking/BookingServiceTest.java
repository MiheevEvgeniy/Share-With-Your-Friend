package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class BookingServiceTest {
    @Mock
    private BookingRepository repository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper mapper;

    @InjectMocks
    private BookingServiceImpl service;

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
    void getAllBookingsByBookerAndState_whenStateIsAll_thenBookingListReturned() {

        BookingStatus bookingStatus = BookingStatus.ALL;

        List<Booking> bookingList = List.of(booking, booking, booking);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.findByStatusInOrderByStartDesc(List.of(BookingStatus.values()))).thenReturn(bookingList);
        List<BookingOutputDto> expectedList = List.of(bookingOutputDto, bookingOutputDto, bookingOutputDto);

        List<BookingOutputDto> actualList = service.getAllBookingsByBookerAndState(booking.getBooker().getId(), bookingStatus, 0, 100);

        verify(userRepository).findById(anyLong());
        verify(repository).findByStatusInOrderByStartDesc(List.of(BookingStatus.values()));
        assertEquals(expectedList, actualList);
    }

    @Test
    void getAllBookingsByBookerAndState_whenStateIsPast_thenBookingListReturned() {

        BookingStatus bookingStatus = BookingStatus.PAST;

        List<Booking> bookingList = List.of(booking, booking, booking);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.findByEndBeforeOrderByStartDesc(any())).thenReturn(bookingList);
        List<BookingOutputDto> expectedList = List.of(bookingOutputDto, bookingOutputDto, bookingOutputDto);

        List<BookingOutputDto> actualList = service.getAllBookingsByBookerAndState(booking.getBooker().getId(), bookingStatus, 0, 100);

        verify(userRepository).findById(anyLong());
        verify(repository).findByEndBeforeOrderByStartDesc(any());
        assertEquals(expectedList, actualList);
    }

    @Test
    void getAllBookingsByBookerAndState_whenStateIsCurrent_thenBookingListReturned() {

        BookingStatus bookingStatus = BookingStatus.CURRENT;

        List<Booking> bookingList = List.of(booking, booking, booking);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.findByStartBeforeAndEndAfterOrderByStartDesc(any(), any())).thenReturn(bookingList);
        List<BookingOutputDto> expectedList = List.of(bookingOutputDto, bookingOutputDto, bookingOutputDto);

        List<BookingOutputDto> actualList = service.getAllBookingsByBookerAndState(booking.getBooker().getId(), bookingStatus, 0, 100);

        verify(userRepository).findById(anyLong());
        verify(repository).findByStartBeforeAndEndAfterOrderByStartDesc(any(), any());
        assertEquals(expectedList, actualList);
    }

    @Test
    void getAllBookingsByBookerAndState_whenStateIsFuture_thenBookingListReturned() {

        BookingStatus bookingStatus = BookingStatus.FUTURE;

        List<Booking> bookingList = List.of(booking, booking, booking);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.findByStartAfterOrderByStartDesc(any())).thenReturn(bookingList);
        List<BookingOutputDto> expectedList = List.of(bookingOutputDto, bookingOutputDto, bookingOutputDto);

        List<BookingOutputDto> actualList = service.getAllBookingsByBookerAndState(booking.getBooker().getId(), bookingStatus, 0, 100);

        verify(userRepository).findById(anyLong());
        verify(repository).findByStartAfterOrderByStartDesc(any());
        assertEquals(expectedList, actualList);
    }

    @Test
    void getAllBookingsByBookerAndState_whenStateIsSomethingDifferent_thenBookingListReturned() {

        BookingStatus bookingStatus = BookingStatus.CANCELED;

        List<Booking> bookingList = List.of(booking, booking, booking);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.findByStatusInOrderByStartDesc(List.of(bookingStatus))).thenReturn(bookingList);
        List<BookingOutputDto> expectedList = List.of(bookingOutputDto, bookingOutputDto, bookingOutputDto);

        List<BookingOutputDto> actualList = service.getAllBookingsByBookerAndState(booking.getBooker().getId(), bookingStatus, 0, 100);

        verify(userRepository).findById(anyLong());
        verify(repository).findByStatusInOrderByStartDesc(List.of(bookingStatus));
        assertEquals(expectedList, actualList);
    }

    @Test
    void getAllBookingsByBookerAndState_whenStateIsUnsupportedStatus_thenBookingListReturned() {
        BookingStatus bookingStatus = BookingStatus.UNSUPPORTED_STATUS;
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> service.getAllBookingsByBookerAndState(booking.getBooker().getId(), bookingStatus, 0, 100));
        verify(userRepository).findById(anyLong());
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getAllBookingsByBookerAndState_whenUserIsEmpty_thenBookingListReturned() {
        BookingStatus bookingStatus = BookingStatus.UNSUPPORTED_STATUS;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class,
                () -> service.getAllBookingsByBookerAndState(booking.getBooker().getId(), bookingStatus, 0, 100));
        verify(userRepository).findById(anyLong());
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getAllBookingsByOwnerAndState_whenStateIsAll_thenBookingListReturned() {

        BookingStatus bookingStatus = BookingStatus.ALL;

        List<Booking> bookingList = List.of(booking, booking, booking);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.findByStatusInOrderByStartDesc(List.of(BookingStatus.values()))).thenReturn(bookingList);
        List<BookingOutputDto> expectedList = List.of(bookingOutputDto, bookingOutputDto, bookingOutputDto);

        List<BookingOutputDto> actualList = service.getAllBookingsByOwnerAndState(booking.getBooker().getId(), bookingStatus, 0, 100);

        verify(userRepository).findById(anyLong());
        verify(repository).findByStatusInOrderByStartDesc(List.of(BookingStatus.values()));
        assertEquals(expectedList, actualList);
    }

    @Test
    void getBookingById_whenInvoked_thenBookingReturned() {
        when(repository.getReferenceById(anyLong())).thenReturn(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        BookingOutputDto expectedBooking = bookingOutputDto;

        BookingOutputDto actualBooking = service.getBookingById(item.getOwner().getId(), item.getId());

        verify(repository).getReferenceById(item.getId());
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void getBookingById_whenUserIsNotValid_thenBookingReturned() {
        when(repository.getReferenceById(anyLong())).thenReturn(booking);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class,
                () -> service.getBookingById(item.getOwner().getId(), item.getId()));

        verify(repository).getReferenceById(item.getId());
        verify(mapper, never()).toOutputDtoFromEntity(any());
        assertEquals("Пользователь не найден или не является автором бронирования или владельцем вещи", exception.getMessage());
    }

    @Test
    void approveBooking_whenApprove_thenBookingApproved() {
        Booking notApprovedBooking = booking;
        notApprovedBooking.setStatus(BookingStatus.WAITING);
        when(repository.getById(anyLong())).thenReturn(notApprovedBooking);
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.save(any())).thenReturn(booking);

        BookingOutputDto actualBooking = service.approveBooking(booking.getBooker().getId(), true, booking.getId());

        verify(repository).save(any());
        assertEquals(actualBooking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_whenReject_thenBookingRejected() {
        when(repository.getById(anyLong())).thenReturn(booking);
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(repository.save(any())).thenReturn(booking);

        BookingOutputDto actualBooking = service.approveBooking(booking.getBooker().getId(), false, booking.getId());

        verify(repository).save(any());
        assertEquals(actualBooking.getStatus(), BookingStatus.REJECTED);

    }

    @Test
    void approveBooking_whenIsAlreadyApproved_thenThrowUnsupportedStatusException() {
        Booking approvedBooking = booking;
        approvedBooking.setStatus(BookingStatus.APPROVED);
        when(repository.getById(anyLong())).thenReturn(approvedBooking);

        UnsupportedStatusException exception = assertThrows(UnsupportedStatusException.class,
                () -> service.approveBooking(approvedBooking.getBooker().getId(), true, approvedBooking.getId()));

        verify(repository, never()).save(any());
        assertEquals("Одобрить дважды нельзя", exception.getMessage());
    }

    @Test
    void approveBooking_whenUserIsNotOwner_thenThrowOwnerNotFoundException() {
        when(repository.getById(anyLong())).thenReturn(booking);

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class,
                () -> service.approveBooking(100L, true, booking.getId()));

        verify(repository, never()).save(any());
        assertEquals("Пользователь не владелец вещи", exception.getMessage());
    }

    @Test
    void addBooking_whenInvoked_thenBookingSaved() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(mapper.toEntityFromInputDto(any(), any(), any(), any())).thenReturn(booking);
        when(repository.save(any())).thenReturn(booking);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.now())
                .itemId(item.getId())
                .build();
        BookingOutputDto expectedBooking = bookingOutputDto;

        BookingOutputDto actualBooking = service.addBooking(100L, bookingInputDto);
        verify(repository).save(any());
        assertEquals(expectedBooking, actualBooking);
    }

    @Test
    void addBooking_whenItemNotFound_thenThrowItemNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(mapper.toEntityFromInputDto(any(), any(), any(), any())).thenReturn(booking);
        when(repository.save(any())).thenReturn(booking);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.now())
                .itemId(item.getId())
                .build();

        ItemNotFoundException exception = assertThrows(ItemNotFoundException.class,
                () -> service.addBooking(100L, bookingInputDto));
        verify(repository, never()).save(any());
        assertEquals("item не существует", exception.getMessage());
    }

    @Test
    void addBooking_whenUserNotFound_thenThrowOwnerNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(mapper.toEntityFromInputDto(any(), any(), any(), any())).thenReturn(booking);
        when(repository.save(any())).thenReturn(booking);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.now())
                .itemId(item.getId())
                .build();

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class,
                () -> service.addBooking(100L, bookingInputDto));
        verify(repository, never()).save(any());
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void addBooking_whenItemIsNotAvailable_thenThrowUnavailableItemException() {
        Item notAvailableItem = new Item();
        notAvailableItem.setOwner(user);
        notAvailableItem.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(notAvailableItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(mapper.toEntityFromInputDto(any(), any(), any(), any())).thenReturn(booking);
        when(repository.save(any())).thenReturn(booking);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.now())
                .itemId(item.getId())
                .build();

        UnavailableItemException exception = assertThrows(UnavailableItemException.class,
                () -> service.addBooking(100L, bookingInputDto));
        verify(repository, never()).save(any());
        assertEquals("item должен быть доступен для бронирования", exception.getMessage());
    }

    @Test
    void addBooking_whenOwnerIsBookingHisOwnItem_thenThrowOwnerNotFoundException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(mapper.toEntityFromInputDto(any(), any(), any(), any())).thenReturn(booking);
        when(repository.save(any())).thenReturn(booking);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.MIN)
                .end(LocalDateTime.now())
                .itemId(item.getId())
                .build();

        OwnerNotFoundException exception = assertThrows(OwnerNotFoundException.class,
                () -> service.addBooking(item.getOwner().getId(), bookingInputDto));
        verify(repository, never()).save(any());
        assertEquals("Пользователь не может арендовать свой же item", exception.getMessage());
    }

    @Test
    void addBooking_whenDurationIsInvalid_thenThrowInvalidBookingDurationException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(mapper.toOutputDtoFromEntity(any())).thenReturn(bookingOutputDto);
        when(mapper.toEntityFromInputDto(any(), any(), any(), any())).thenReturn(booking);
        when(repository.save(any())).thenReturn(booking);
        BookingInputDto bookingInputDto = BookingInputDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .itemId(item.getId())
                .build();

        InvalidBookingDurationException exception = assertThrows(InvalidBookingDurationException.class,
                () -> service.addBooking(100L, bookingInputDto));
        verify(repository, never()).save(any());
        assertEquals("Недопустимая длительность аренды", exception.getMessage());
    }
}
