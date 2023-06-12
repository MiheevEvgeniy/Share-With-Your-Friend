package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comments.model.Comment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemDto {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;

    private Booking lastBooking;
    private Booking nextBooking;
    @Builder.Default

    private List<Comment> comments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDto)) return false;
        return id != null && id.equals(((ItemDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}