package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getItem(Long id);

    void deleteItem(Long id);

    List<Item> getAllItems();

    Item addItem(Item item);

    Item patchItem(Long itemId, Item updatedItem);
}
