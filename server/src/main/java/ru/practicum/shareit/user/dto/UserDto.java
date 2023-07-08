package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserDto {
    private Long id;

    private String email;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDto)) return false;
        return id != null && id.equals(((UserDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}