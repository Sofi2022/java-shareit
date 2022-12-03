package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AlreadyExists;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void validate(User user) {
        List<User> users = userRepository.getAllUsers();
        if (users.contains(user)) {
            throw new AlreadyExists("Такой пользователь уже существует");
        }
        boolean match = users.stream().map(User::getEmail).anyMatch((value) -> value.equals(user.getEmail()));
        if (match) {
            throw new ValidationException("Такой email уже есть");
        }
    }

    @Override
    public User addUser(User user) {
        validate(user);
        return userRepository.saveUser(user);
    }

    @Override
    public User updateUser(long userId, User user) {
        validate(user);
        User newUser = getUserById(userId);
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        userRepository.updateUser(userId, newUser);
        return getUserById(userId);
    }

    @Override
    public User getUserById(long userId) {
        List<Long> userIds = userRepository.getUsersIds();
        if (userIds.contains(userId)) {
            return userRepository.getUserById(userId);
        } else {
            throw new NotFoundException("Такого пользователя нет");
        }
    }

    @Override
    public void deleteUserById(long userId) {
        getUserById(userId);
        userRepository.deleteUserById(userId);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public List<Long> getUsersIds() {
        return userRepository.getUsersIds();
    }
}
