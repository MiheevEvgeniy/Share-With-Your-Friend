package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.InvalidCommentException;
import ru.practicum.shareit.exception.OwnerAccessException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.exception.UnavailableItemException;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentMapper;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemDto> getAllItemsByOwner(long userId) {
        Predicate<Item> itemPredicate = item -> item.getOwner().getId() == userId;
        return repository.findAll()
                .stream()
                .filter(itemPredicate)
                .sorted((Comparator.comparing(Item::getId)))
                .map(item -> mapper.toDto(item,
                        userId,
                        bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now()),
                        bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()),
                        (commentRepository.findByItemId(item.getId())
                                .stream()
                                .map(commentMapper::toDto)
                                .collect(Collectors.toList()))))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        Item item = repository.getById(id);
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        return mapper.toDto(item, userId, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            log.info("Строка поиска пуста. Возвращен пустой ответ");
            return new ArrayList<>();
        }
        return repository
                .findAll()
                .stream()
                .filter(
                        (((Predicate<Item>) item -> item.getName().toLowerCase().contains(text.toLowerCase()))
                                .or(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())))
                                .and(item -> item.getAvailable()))
                .map(item -> mapper.toDto(item,
                        item.getOwner().getId(),
                        bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now()),
                        bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()),
                        (commentRepository.findByItemId(item.getId())
                                .stream()
                                .map(commentMapper::toDto)
                                .collect(Collectors.toList()))))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(ItemDto item, long userId) {
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        return mapper.toDto(repository.save(mapper.toEntity(item, checkUserId(userId))), userId, lastBooking, nextBooking, comments);
    }

    @Override
    public void deleteItem(long id, long userId) {
        checkUserId(userId);
        if (repository.getById(id).getOwner().getId() == userId) {
            repository.deleteById(id);
        } else {
            log.error("Пользователь {} не владелец предмета {}", userId, id);
            throw new OwnerAccessException("Предмет может удалять только его владелец!");
        }
    }

    @Override
    public ItemDto patchItem(long id, ItemDto item, long userId) {
        checkUserId(userId);
        if (repository.getById(id).getOwner().getId().equals(userId)) {
            Item patch = repository.getById(id);
            if (item.getDescription() != null) {
                patch.setDescription(item.getDescription());
            }
            if (item.getName() != null) {
                patch.setName(item.getName());
            }
            if (item.getAvailable() != null) {
                patch.setAvailable(item.getAvailable());
            }
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
            List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                    .stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList());
            return mapper.toDto(repository.save(patch), userId, lastBooking, nextBooking, comments);
        } else {
            log.error("Пользователь {} не владелец предмета {}", userId, id);
            throw new OwnerAccessException("Обновлять предмет может только его владелец");
        }
    }

    @Override
    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        User user = userRepository.getById(userId);
        commentDto.setAuthorName(user.getName());
        LocalDateTime created = LocalDateTime.now();
        commentDto.setCreated(created);
        commentDto.setItemId(itemId);
        Item item = repository.getById(itemId);

        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        List<CommentDto> comments = commentRepository.findByItemId(item.getId())
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
        ItemDto itemDto = mapper.toDto(item, item.getOwner().getId(), lastBooking, nextBooking, comments);
        Boolean userHasBooking = bookingRepository
                .findAll()
                .stream()
                .anyMatch(booking -> booking.getBooker().getId() == userId
                        && booking.getItem().getId() == itemId
                        && !(booking.getStatus().equals(BookingStatus.REJECTED) || booking.getStart().isAfter(created)));
        if (itemDto == null || !userHasBooking) {
            throw new UnavailableItemException("Предмет не найден или на него не было бронирования от указанного пользователя");
        }
        if (commentDto.getText().isEmpty()) {
            throw new InvalidCommentException("Текст комментария отсутствует");
        }
        return commentMapper.toDto(commentRepository.save(commentMapper.toEntity(commentDto, user, item)));
    }

    private User checkUserId(Long userId) {
        try {
            return userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        } catch (EntityNotFoundException e) {
            log.error("Пользователь {} не существует", userId);
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
    }
}
