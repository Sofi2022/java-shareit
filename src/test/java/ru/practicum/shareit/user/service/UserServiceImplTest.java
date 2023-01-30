package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExists;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;


    @Test
    void addUser_WhenUserIs_Valid() {
        User userToSave = new User();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User actualUser = userService.addUser(userToSave);
        assertEquals(userToSave, actualUser);
        verify(userRepository).save(userToSave);
    }

    @Test
    void addUser_WhenUserIs_NotValid() {
        User userToSave = new User(1, "User", "test@email.ru");
        when(userRepository.findAll()).thenReturn(List.of(userToSave));

        assertThrows(AlreadyExists.class, () -> userService.addUser(userToSave));
        verify(userRepository, never()).save(userToSave);
    }

    @Test
    void validateUser_Ok(){
        User user = new User();
        user.setName("ValidName");
        user.setEmail("ValidEmail@yandex.ru");
        userService.validate(user);
    }

    @Test
    void validateUser_Fail(){
        User user = new User();
        user.setName("ValidName");
        user.setEmail("ValidEmail@yandex.ru");

        User sameUser = new User();
        sameUser.setName("ValidName");
        sameUser.setEmail("ValidEmail@yandex.ru");

        when(userRepository.findAll()).thenReturn(List.of(user));
        assertThrows(AlreadyExists.class, () -> userService.validate(sameUser));
    }


    @Test
    void updateUserWhenUser_Found() {
        long userId = 0;
        User oldUser = new User();
        oldUser.setEmail("email@yandex.ru");
        oldUser.setName("name");

        User newUser = new User();
        newUser.setName("name update");
        newUser.setEmail("emailUpdated@yandex.ru");

        when(userRepository.findAll()).thenReturn(List.of(oldUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));


        userService.updateUser(userId, newUser);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals("name update", savedUser.getName());
        assertEquals("emailUpdated@yandex.ru", savedUser.getEmail());
    }

    @Test
    void updateUserWhenUser_NotFound() {
        long userId = 0;
        User newUser = new User();
        newUser.setName("name update");
        newUser.setEmail("emailUpdated@yandex.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, newUser));
        verify(userRepository, never()).save(newUser);
    }

    @Test
    void getUserById_WhenUserFound() {
        long userId = 0;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(userId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserById_WhenUserNotFound() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void deleteUserByIdWhenUser_Found() {
        long userId = 1;
        User newUser = new User();
        newUser.setName("name update");
        newUser.setEmail("emailUpdated@yandex.ru");


        when(userRepository.findById(userId)).thenReturn(Optional.of(newUser));
        userService.deleteUserById(userId);
        verify(userRepository, atLeast(1)).deleteById(userId);
    }

    @Test
    void deleteUserByIdWhenUser_NotFound() {
        long userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, never()).deleteById(userId);

        List<User> users = userService.getUsers();
        assertEquals(0, users.size());
    }

    @Test
    void getUsers() {
        User newUser = new User();
        newUser.setId(1);
        newUser.setName("name");
        newUser.setEmail("emailUpdated@yandex.ru");

        User user2 = new User();
        user2.setId(2);
        user2.setName("New user");
        user2.setEmail("NewEmail@yandex.ru");

        when(userRepository.findAll()).thenReturn(List.of(newUser, user2));
        List<User> users = userService.getUsers();
        assertNotNull(users);
        assertEquals(users.get(0).getId(), newUser.getId());
    }

    @Test
    void getUsersIds() {
        User newUser = new User();
        newUser.setId(1);
        newUser.setName("name");
        newUser.setEmail("emailUpdated@yandex.ru");

        User user2 = new User();
        user2.setId(2);
        user2.setName("New user");
        user2.setEmail("NewEmail@yandex.ru");

        when(userRepository.getUsersIds()).thenReturn(List.of(newUser.getId(), user2.getId()));
        List<Long> userIds = userService.getUsersIds();
        assertNotNull(userIds);
        assertEquals(userIds.get(0), newUser.getId());
        assertEquals(userIds.get(1), user2.getId());
    }

    @Test
    void getUsersIdsEmpty() {
        long userId = 0;

        when(userRepository.getUsersIds()).thenReturn(new ArrayList<>());
        List<Long> userIds = userService.getUsersIds();
        assertEquals(0, userIds.size());
    }
}