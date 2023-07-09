package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItemsByOwner(long userId) {

        return get("", userId);
    }

    public ResponseEntity<Object> addItem(ItemDto requestDto, long userId) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> deleteItem(long id, long userId) {
        return delete("/" + id, userId);
    }

    public ResponseEntity<Object> patchItem(long id, ItemDto requestDto, long userId) {
        return patch("/" + id, userId, requestDto);
    }

    public ResponseEntity<Object> addComment(long itemId, long userId, CommentDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }

    public ResponseEntity<Object> searchItem(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", parameters);
    }

    public ResponseEntity<Object> getItem(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
}
