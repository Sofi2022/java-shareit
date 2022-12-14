package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);

    User updateUser(long userId, User user);

    User getUserById(long userId);

    void deleteUserById(long userId);

    List<User> getAllUsers();

    List<Long> getUsersIds();
}
