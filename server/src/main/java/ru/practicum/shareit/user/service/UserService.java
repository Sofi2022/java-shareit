package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User updateUser(Long userId, User user);

    User getUserById(long userId);

    void deleteUserById(long userId);

    List<User> getUsers();

    List<Long> getUsersIds();

    void validate(User user);
}
