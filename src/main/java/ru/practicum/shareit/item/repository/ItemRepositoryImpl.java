package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    private Long generatedId = 0L;

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
    public Item addItem(Item item) {
        item.setId(++generatedId);
        items.put(generatedId, item);
        return item;
    }

    @Override
    public Item patchItem(Long itemId, Item patch) {
        items.put(itemId, patch);
        return patch;
    }
}
