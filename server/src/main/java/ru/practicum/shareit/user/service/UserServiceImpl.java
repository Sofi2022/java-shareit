package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExists;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void validate(User user) {
        List<User> users = userRepository.findAll();
        if (users.contains(user)) {
            throw new AlreadyExists("Такой пользователь уже существует " + user);
        }
    }

    @Transactional
    @Override
    public User addUser(User user) {
        validate(user);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(Long userId, User user) {
        validate(user);
        User newUser = getUserById(userId);
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        userRepository.save(newUser);
        return getUserById(userId);
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId));
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        getUserById(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Long> getUsersIds() {
        return userRepository.getUsersIds();
    }
}
