package ru.practicum.shareit.item.comments.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toEntity(CommentDto commentDto, User author, Item item) {
        if (commentDto != null) {
            Comment comment = new Comment();
            comment.setId(commentDto.getId());
            comment.setText(commentDto.getText());
            comment.setAuthor(author);
            comment.setItem(item);
            comment.setCreated(commentDto.getCreated());
            return comment;
        }
        return null;
    }
}
