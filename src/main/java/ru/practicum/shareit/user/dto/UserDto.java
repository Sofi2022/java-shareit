package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class UserDto {

    private long id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @Email
    @NotNull
    private String email;
}
