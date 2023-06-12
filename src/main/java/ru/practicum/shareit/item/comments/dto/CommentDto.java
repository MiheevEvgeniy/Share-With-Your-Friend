package ru.practicum.shareit.item.comments.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;

    private String text;

    private String authorName;
    private Long itemId;

    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentDto)) return false;
        return id != null && id.equals(((CommentDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
