package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private long id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @Email
    @NotNull
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
