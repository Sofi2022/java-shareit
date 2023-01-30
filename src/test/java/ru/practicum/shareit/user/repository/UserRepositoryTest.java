package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    private void addUsers(){
        userRepository.save(new User(1, "Name", "User1@email.ru"));
        userRepository.save(new User(2, "Name2", "User2@email.ru"));
        userRepository.save(new User(3, "Name3", "User3@email.ru"));


    }

    @Test
    void get3UsersIds() {
        List<Long> actualIds = userRepository.getUsersIds();

        assertFalse(actualIds.isEmpty());
        assertEquals(1, actualIds.get(0));
        assertEquals(2, actualIds.get(1));
        assertEquals(3, actualIds.get(2));
    }

    @Test
    void getEmptyUsersIds() {
        userRepository.deleteAll();
        List<Long> actualIds = userRepository.getUsersIds();

        assertTrue(actualIds.isEmpty());
    }

    @Test
    void getOneUsersIds() {
        userRepository.deleteAll();
        userRepository.save(new User(4, "Name", "nweUser@email.ru"));

        List<Long> actualIds = userRepository.getUsersIds();

        assertFalse(actualIds.isEmpty());
        assertEquals(4, actualIds.get(0));
    }

    @AfterEach
    private void deleteUsers(){
        userRepository.deleteAll();
    }
}