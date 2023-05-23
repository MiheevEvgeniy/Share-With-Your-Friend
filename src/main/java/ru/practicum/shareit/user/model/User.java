package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    @Builder.Default
    private Long id = 1L;

    @NotNull
    @NotBlank
    @Email(regexp = "^(.+)@(\\S+)$")
    private String email;

    @NotBlank
    private String name;

}
