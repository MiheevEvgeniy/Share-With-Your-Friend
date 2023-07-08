package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ItemDtoForRequest {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDtoForRequest)) return false;
        return id != null && id.equals(((ItemDtoForRequest) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}