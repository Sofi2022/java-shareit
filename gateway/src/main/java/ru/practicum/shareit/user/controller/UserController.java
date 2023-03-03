package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        return userClient.createUser(userDto);
    }

//    @PatchMapping("/{userId}")
//    public UserDto updateUser(@PathVariable long userId, @RequestBody UpdateUserDto userDto) {
//        User user = userService.updateUser(userId, UserMapper.toUser(userDto));
//        return UserMapper.toUserDto(user);
//    }
//
//    @GetMapping("/{userId}")
//    public UserDto getUserById(@PathVariable long userId) {
//        return UserMapper.toUserDto(userService.getUserById(userId));
//    }
//
//    @DeleteMapping("/{userId}")
//    void deleteUserById(@PathVariable long userId) {
//        userService.deleteUserById(userId);
//    }
//
//    @GetMapping
//    List<UserDto> getUsers() {
//        return userService.getUsers().stream()
//                .map(UserMapper::toUserDto)
//                .collect(Collectors.toList());
//    }
}
