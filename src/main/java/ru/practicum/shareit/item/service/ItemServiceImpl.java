package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.OwnerAccessException;
import ru.practicum.shareit.exception.OwnerNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public List<ItemDto> getAllItemsByOwner(long userId) {
        Predicate<Item> itemPredicate = item -> item.getOwnerId() == userId;

        return repository.getAllItems()
                .stream()
                .filter(itemPredicate)
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long id) {
        return mapper.map(repository.getItem(id));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            log.info("Строка поиска пуста. Возвращен пустой ответ");
            return new ArrayList<>();
        }
        return repository
                .getAllItems()
                .stream()
                .filter(
                        (((Predicate<Item>) item -> item.getName().toLowerCase().contains(text.toLowerCase()))
                                .or(item -> item.getDescription().toLowerCase().contains(text.toLowerCase())))
                                .and(item -> item.getAvailable()))
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(ItemDto item, long userId) {
        if (!isUserExist(userRepository.getUser(userId))) {
            log.error("Пользователь {} не существует", userId);
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
        return mapper.map(repository.addItem(item, userId));
    }

    @Override
    public void deleteItem(long id, long userId) {
        if (!isUserExist(userRepository.getUser(userId))) {
            log.error("Пользователь {} не существует", userId);
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
        if (repository.getItem(id).getOwnerId() == userId) {
            repository.deleteItem(id);
        } else {
            log.error("Пользователь {} не владелец предмета {}", userId, id);
            throw new OwnerAccessException("Предмет может удалять только его владелец!");
        }
    }

    @Override
    public ItemDto patchItem(long id, Item item) {
        if (!isUserExist(userRepository.getUser(item.getOwnerId()))) {
            log.error("Пользователь {} не существует", item.getOwnerId());
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
        if (repository.getItem(id).getOwnerId().equals(item.getOwnerId())) {
            return mapper.map(repository.patchItem(id, item));
        } else {
            log.error("Пользователь {} не владелец предмета {}", item.getOwnerId(), id);
            throw new OwnerAccessException("Обновлять предмет может только его владелец");
        }
    }

    private boolean isUserExist(User user) {
        if (user == null) {
            return false;
        }
        return true;
    }
}
