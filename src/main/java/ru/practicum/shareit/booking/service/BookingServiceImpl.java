package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper mapper;

    @Override
    public BookingOutputDto addBooking(long bookerId, BookingInputDto bookingInputDto) {
        bookingInputDto.setBookerId(bookerId);
        bookingInputDto.setStatus(BookingStatus.WAITING);
        Optional<Item> item = itemRepository.findById(bookingInputDto.getItemId());
        Optional<User> user = userRepository.findById(bookerId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException("item не существует");
        }
        if (user.isEmpty()) {
            throw new OwnerNotFoundException("Пользователь не найден");
        }
        if (item.get().getOwnerId() == bookerId) {
            throw new OwnerNotFoundException("Пользователь не может арендовать свой же item");
        }
        if (!item.get().getAvailable()) {
            log.error("item {} не доступен для бронирования", bookingInputDto.getItemId());
            throw new UnavailableItemException("item должен быть доступен для бронирования");
        }
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart()) || bookingInputDto.getStart().isEqual(bookingInputDto.getEnd())) {
            throw new InvalidBookingDurationException("Недопустимая длительность аренды");
        }

        return mapper.toOutputDtoFromEntity(repository.save(mapper.toEntityFromInputDto(bookingInputDto)));
    }

    @Override
    public BookingOutputDto approveBooking(long ownerId, Boolean approved, Long bookingId) {
        Booking booking = repository.getById(bookingId);
        BookingOutputDto bookingOutputDto = mapper.toOutputDtoFromEntity(booking);
        Long itemOwnerId = bookingOutputDto.getItem().getOwnerId();
        log.info("patch id владельца предмета: {}, id пользователя {}", booking.getBookerId(), ownerId);
        if (itemOwnerId != ownerId) {
            throw new OwnerNotFoundException("Пользователь не владелец вещи");
        }
        if (booking.getStatus() == BookingStatus.APPROVED && approved) {
            throw new UnsupportedStatusException("Одобрить дважды нельзя");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingOutputDto.setStatus(booking.getStatus());
        repository.save(booking);
        return bookingOutputDto;
    }

    @Override
    public BookingOutputDto getBookingById(long bookerId, Long bookingId) {
        Booking booking = repository.getReferenceById(bookingId);
        BookingOutputDto bookingOutputDto = mapper.toOutputDtoFromEntity(booking);
        Long itemOwnerId = bookingOutputDto.getItem().getOwnerId();
        log.info("id владельца предмета: {}, id пользователя {}", booking.getBookerId(), bookerId);
        Optional<User> user = userRepository.findById(bookerId);
        if (user.isEmpty() || (booking.getBookerId() != bookerId && itemOwnerId != bookerId)) {
            throw new OwnerNotFoundException("Пользователь не найден или не является автором бронирования или владельцем вещи");
        }
        return bookingOutputDto;
    }

    @Override
    public List<BookingOutputDto> getAllBookingsByBookerAndState(long bookerId, BookingStatus state) {
        return getAllBookingsByState(bookerId, state)
                .stream()
                .filter(booking -> booking.getBooker().getId() == bookerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingOutputDto> getAllBookingsByOwnerAndState(long bookerId, BookingStatus state) {
        return getAllBookingsByState(bookerId, state)
                .stream()
                .filter(booking -> booking.getItem().getOwnerId() == bookerId)
                .collect(Collectors.toList());
    }

    private List<BookingOutputDto> getAllBookingsByState(long bookerId, BookingStatus state) {
        Optional<User> user = userRepository.findById(bookerId);
        if (user.isEmpty()) {
            throw new OwnerNotFoundException("Пользователь не найден");
        }
        switch (state) {
            case ALL:
                return repository.findByStatusInOrderByStartDesc(List.of(BookingStatus.values()))
                        .stream()
                        .map(mapper::toOutputDtoFromEntity)
                        .collect(Collectors.toList());
            case PAST:
                return repository.findByEndBeforeOrderByStartDesc(LocalDateTime.now())
                        .stream()
                        .map(mapper::toOutputDtoFromEntity)
                        .collect(Collectors.toList());
            case CURRENT:
                return repository.findByStartBeforeAndEndAfterOrderByStartDesc(LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(mapper::toOutputDtoFromEntity)
                        .collect(Collectors.toList());
            case FUTURE:
                return repository.findByStartAfterOrderByStartDesc(LocalDateTime.now())
                        .stream()
                        .map(mapper::toOutputDtoFromEntity)
                        .collect(Collectors.toList());
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
            default:
                return repository.findByStatusInOrderByStartDesc(List.of(state))
                        .stream()
                        .map(mapper::toOutputDtoFromEntity)
                        .collect(Collectors.toList());
        }
    }
}
