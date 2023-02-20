package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@WebMvcTest({UserController.class, UserMapper.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    private static final String PATH = "/users";

    @Test
    void getUsers() throws Exception {
        User user = new User();
        user.setId(1);
        user.setName("UserTest");
        user.setEmail("UserTest@email.ru");

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(new User(2L, "newUser", "NewUser123@mail.ru"));

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        //var result = userService.getUsers();

//        assertNotNull(result);
//        assertEquals(user.getId(), result.get(0).getId());
    }

    @Test
    void addUser() throws Exception {
        User user = new User(1, "UserTest", "UserTest@email.ru");
        when(userService.addUser(any())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("user/request/create.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(getContentFromFile("user/response/created.json")));

    }

    @Test
    void updateUser() throws Exception {
        User user = new User(1, "UserTestUpdate", "UserTest@email.ru");
        when(userService.updateUser(anyLong(), any())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContentFromFile("user/request/update.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(getContentFromFile("user/response/updated.json")));
    }

    @Test
    void getUserById_WhenUserNotFound() throws Exception {
        long wrongId = 10L;
        when(userService.getUserById(anyLong())).thenThrow(new ValidationException("Unknown userId:" + wrongId));
        mockMvc.perform(MockMvcRequestBuilders.get("/users/10L"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_WhenUserFound() throws Exception {
        User expectedUser = new User(1L, "Olya", "Olya123@mail.ru");
        when(userService.getUserById(anyLong())).thenReturn(expectedUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUser.getId()))
                .andExpect(jsonPath("$.name").value("Olya"))
                .andExpect(jsonPath("$.email").value("Olya123@mail.ru"));
    }

    @Test
    void deleteUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", 1))
                .andExpect(status().isOk());
        verify(userService, Mockito.times(1)).deleteUserById(1);
    }

    private String getContentFromFile(final String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(),
                    StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new RuntimeException("Unable to open file", exception);
        }
    }
}