package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.support.PagedListHolder;
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
import java.util.ArrayList;
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
        Optional<Item> item = itemRepository.findById(bookingInputDto.getItemId());
        Optional<User> user = userRepository.findById(bookerId);
        if (item.isEmpty()) {
            throw new ItemNotFoundException("item не существует");
        }
        if (user.isEmpty()) {
            throw new OwnerNotFoundException("Пользователь не найден");
        }
        if (item.get().getOwner().getId() == bookerId) {
            throw new OwnerNotFoundException("Пользователь не может арендовать свой же item");
        }
        if (!item.get().getAvailable()) {
            log.error("item {} не доступен для бронирования", bookingInputDto.getItemId());
            throw new UnavailableItemException("item должен быть доступен для бронирования");
        }
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart()) || bookingInputDto.getStart().isEqual(bookingInputDto.getEnd())) {
            throw new InvalidBookingDurationException("Недопустимая длительность аренды");
        }
        BookingOutputDto bookingOutputDto = mapper.toOutputDtoFromEntity(repository.save(mapper.toEntityFromInputDto(bookingInputDto,
                item.get(),
                user.get(),
                BookingStatus.WAITING)));
        bookingOutputDto.setStatus(BookingStatus.WAITING);
        bookingOutputDto.setBooker(user.get());
        return bookingOutputDto;
    }

    @Override
    public BookingOutputDto approveBooking(long ownerId, Boolean approved, Long bookingId) {
        Booking booking = repository.getById(bookingId);
        log.info("patch id владельца предмета: {}, id пользователя {}", booking.getBooker().getId(), ownerId);
        if (booking.getItem().getOwner().getId() != ownerId) {
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
        BookingOutputDto bookingOutputDto = mapper.toOutputDtoFromEntity(booking);
        bookingOutputDto.setStatus(booking.getStatus());
        repository.save(booking);
        return bookingOutputDto;
    }

    @Override
    public BookingOutputDto getBookingById(long bookerId, Long bookingId) {
        Booking booking = repository.getReferenceById(bookingId);
        log.info("id владельца предмета: {}, id пользователя {}", booking.getBooker().getId(), bookerId);
        Optional<User> user = userRepository.findById(bookerId);
        if (user.isEmpty() || (booking.getBooker().getId() != bookerId && booking.getItem().getOwner().getId() != bookerId)) {
            throw new OwnerNotFoundException("Пользователь не найден или не является автором бронирования или владельцем вещи");
        }
        return mapper.toOutputDtoFromEntity(booking);
    }

    @Override
    public List<BookingOutputDto> getAllBookingsByBookerAndState(long bookerId, BookingStatus state, Integer from, Integer size) {
        PagedListHolder page = new PagedListHolder(getAllBookingsByState(bookerId, state)
                .stream()
                .filter(booking -> booking.getBooker().getId() == bookerId)
                .collect(Collectors.toList()));
        page.setPageSize(size);
        page.setPage(from);
        return new ArrayList<>(page.getPageList());
    }

    @Override
    public List<BookingOutputDto> getAllBookingsByOwnerAndState(long bookerId, BookingStatus state, Integer from, Integer size) {
        PagedListHolder page = new PagedListHolder(getAllBookingsByState(bookerId, state)
                .stream()
                .filter(booking -> booking.getItem().getOwner().getId() == bookerId)
                .collect(Collectors.toList()));
        page.setPageSize(size);
        page.setPage(from);
        return new ArrayList<>(page.getPageList());
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
