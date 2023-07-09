package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("id владельца для запроса getAllItemsByOwner: {}", userId);
        ResponseEntity<Object> allItems = client.getAllItemsByOwner(userId);
        log.info("Результат запроса getAllItemsByOwner: {}", allItems);
        return allItems;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long id) {
        log.info("id для запроса getItem: {}", id);
        ResponseEntity<Object> item = client.getItem(userId, id);
        log.info("Результат запроса getItem: {}", item);
        return item;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        log.info("text для запроса searchItem: {}", text);
        ResponseEntity<Object> result = client.searchItem(text);
        log.info("Результат запроса searchItem: {}", result);
        return result;
    }

    @PostMapping()
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto item,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("item и id владельца для запроса addItem: {}, {}", item, userId);
        ResponseEntity<Object> result = client.addItem(item, userId);
        log.info("Результат запроса addItem: {}", result);
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Удаление предмета с id {} и id владельца {} начато", id, userId);
        client.deleteItem(id, userId);
        log.info("Удаление предмета с id {} и id владельца {} завершено", id, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@PathVariable long id,
                                            @RequestBody ItemDto patch,
                                            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("patchItem: id {}, item {}, userId {} патч начат", id, patch, userId);
        ResponseEntity<Object> itemDto = client.patchItem(id, patch, userId);
        log.info("Патч завершен. Результат: {}", itemDto);
        return itemDto;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody CommentDto commentDto) {
        log.info("addComment: id {}, commentDto {}, userId {} патч начат", itemId, commentDto, userId);
        ResponseEntity<Object> result = client.addComment(itemId, userId, commentDto);
        log.info("Добавление комметария завершено. Результат: {}", result);
        return result;
    }
}
