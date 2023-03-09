package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Data
public class UpdateUserDto {

    private long id;

    private String name;

    @Email
    private String email;
}
