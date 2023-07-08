package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotNull
    @Email(regexp = "^(.+)@(\\S+)$")
    private String email;

    @NotBlank
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