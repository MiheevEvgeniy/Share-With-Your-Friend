package ru.practicum.shareit.item.comments.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.comments.model.Comment;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment, Long itemId) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthorName())
                .itemId(itemId)
                .created(comment.getCreated())
                .build();
    }

    public Comment toEntity(CommentDto commentDto, Long itemId) {
        if (commentDto != null) {
            Comment comment = new Comment();
            comment.setId(commentDto.getId());
            comment.setItemId(itemId);
            comment.setText(commentDto.getText());
            comment.setAuthorName(commentDto.getAuthorName());
            comment.setCreated(commentDto.getCreated());
            return comment;
        }
        return null;
    }
}
