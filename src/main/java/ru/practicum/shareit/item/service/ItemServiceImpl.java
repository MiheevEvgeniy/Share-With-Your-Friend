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
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(long id) {
        return mapper.toDto(repository.getItem(id));
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
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto addItem(ItemDto item, long userId) {
        if (!isUserExist(userRepository.getUser(userId))) {
            log.error("Пользователь {} не существует", userId);
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
        return mapper.toDto(repository.addItem(mapper.toEntity(item, userId)));
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
    public ItemDto patchItem(long id, ItemDto item, long userId) {
        if (!isUserExist(userRepository.getUser(userId))) {
            log.error("Пользователь {} не существует", userId);
            throw new OwnerNotFoundException("Владелец предмета не найден");
        }
        if (repository.getItem(id).getOwnerId().equals(userId)) {
            Item patch = repository.getItem(id);
            if (item.getDescription() != null) {
                patch.setDescription(item.getDescription());
            }
            if (item.getName() != null) {
                patch.setName(item.getName());
            }
            if (item.getAvailable() != null) {
                patch.setAvailable(item.getAvailable());
            }
            return mapper.toDto(repository.patchItem(id, patch));
        } else {
            log.error("Пользователь {} не владелец предмета {}", userId, id);
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
