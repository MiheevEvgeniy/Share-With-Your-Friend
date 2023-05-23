package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItemsByOwner(long userId);

    ItemDto getItem(long id);

    List<ItemDto> searchItem(String text);

    ItemDto addItem(ItemDto item, long userId);

    void deleteItem(long id, long userId);

    ItemDto patchItem(long id, Item item);
}
