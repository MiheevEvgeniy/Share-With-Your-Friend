package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {

    private Long id;

    @NotBlank
    @Email(regexp = "^(.+)@(\\S+)$")
    private String email;

    @NotBlank
    private String name;

}
