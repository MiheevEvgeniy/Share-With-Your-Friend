package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        Predicate<Item> itemPredicate = item -> item.getOwnerId() == userId;
        return repository.findAll()
                .stream()
                .filter(itemPredicate)
                .sorted((Comparator.comparing(Item::getId)))
                .map(item -> mapper.toDto(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long userId, long id) {
        return mapper.toDto(repository.getById(id), userId);
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
                .map(item -> mapper.toDto(item, item.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(ItemDto item, long userId) {
        checkUserId(userId);
        return mapper.toDto(repository.save(mapper.toEntity(item, userId)), userId);
    }

    @Override
    public void deleteItem(long id, long userId) {
        checkUserId(userId);
        if (repository.getById(id).getOwnerId() == userId) {
            repository.deleteById(id);
        } else {
            log.error("Пользователь {} не владелец предмета {}", userId, id);
            throw new OwnerAccessException("Предмет может удалять только его владелец!");
        }
    }

    @Override
    public ItemDto patchItem(long id, ItemDto item, long userId) {
        checkUserId(userId);
        if (repository.getById(id).getOwnerId().equals(userId)) {
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
            return mapper.toDto(repository.save(patch), userId);
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
        ItemDto itemDto = mapper.toDto(item, item.getOwnerId());
        Boolean userHasBooking = bookingRepository
                .findAll()
                .stream()
                .anyMatch(booking -> booking.getBookerId() == userId
                        && booking.getItemId() == itemId
                        && !(booking.getStatus().equals(BookingStatus.REJECTED) || booking.getStart().isAfter(created)));
        if (itemDto == null || !userHasBooking) {
            throw new UnavailableItemException("Предмет не найден или на него не было бронирования от указанного пользователя");
        }
        if (commentDto.getText().isEmpty()) {
            throw new InvalidCommentException("Текст комментария отсутствует");
        }
        return commentMapper.toDto(commentRepository.save(commentMapper.toEntity(commentDto, itemId)), itemId);
    }

    private void checkUserId(Long userId) {
        try {
            userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        } catch (EntityNotFoundException e) {
            log.error("Пользователь {} не существует", userId);
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
    }
}
