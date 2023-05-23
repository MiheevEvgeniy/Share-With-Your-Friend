package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private static final Map<Long, Item> items = new HashMap<>();

    private static Long generatedId = 1L;

    @Override
    public Item getItem(Long id) {
        return items.get(id);
    }

    @Override
    public void deleteItem(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        Item item = Item.builder()
                .name(itemDto.getName())
                .id(generatedId)
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(userId)
                .build();
        items.put(generatedId, item);
        ++generatedId;
        return item;
    }

    @Override
    public Item patchItem(Long itemId, Item patch) {
        Item item = getItem(itemId);
        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getName() != null) {
            item.setName(patch.getName());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        items.put(itemId, item);
        return item;
    }
}
