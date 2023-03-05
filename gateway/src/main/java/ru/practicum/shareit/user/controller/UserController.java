package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Gateway : вызван метод addUser");
       return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UpdateUserDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Gateway : вызван метод getUserById");
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    void deleteUserById(@PathVariable long userId) {
        log.info("Gateway : вызван метод deleteUserById");
        userClient.deleteUserById(userId);
    }

    @GetMapping
    ResponseEntity<Object> getUsers() {
        log.info("Gateway : вызван метод getUsers");
        return userClient.getUsers();
    }
}


