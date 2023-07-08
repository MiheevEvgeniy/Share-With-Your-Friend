package ru.practicum.shareit.item.comments;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.mapper.CommentMapper;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentMapperTest {
    @InjectMocks
    private CommentMapper mapper;

    private static Item item = new Item();
    private static User user = new User();
    private static Comment comment = new Comment();

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("comment text")
            .created(LocalDateTime.now())
            .itemId(1L)
            .authorName("Sam")
            .build();

    @BeforeAll
    static void createData() {
        long userId = 1L;
        user.setId(userId);
        user.setName("Sam");
        user.setEmail("sam@gmail.com");

        long itemId = 1L;
        item.setName("item2");
        item.setDescription("description2");
        item.setAvailable(true);
        item.setId(itemId);
        item.setOwner(user);

        long commentId = 1L;
        comment.setId(commentId);
        comment.setText("comment text");
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        comment.setAuthor(user);
    }

    @Test
    void toDto_whenInvoked_thenCommentCastedToCommentDto() {

        CommentDto actualDto = mapper.toDto(comment);

        assertEquals(commentDto, actualDto);
    }

    @Test
    void toEntity_whenInvoked_thenCommentDtoCastedToComment() {

        Comment actualComment = mapper.toEntity(commentDto, user, item);

        assertEquals(comment, actualComment);
    }

    @Test
    void toEntity_whenDtoIsNull_thenNullReturned() {
        Comment actualComment = mapper.toEntity(null, null, null);

        assertNull(actualComment);
    }

}
