package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {

    private long userId = 0;

    private Map<Long, User> users = new HashMap<>();

    @Override
    public User saveUser(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        users.put(userId, user);
        return getUserById(userId);
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        users.remove(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public List<Long> getUsersIds() {
        return List.copyOf(users.keySet());
    }
}
