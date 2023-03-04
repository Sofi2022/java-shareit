package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({ItemController.class, ItemMapper.class, CommentMapper.class})
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemMapper itemMapper;

    @MockBean
    private ItemServiceImpl itemService;

    private Item item1;

    private Item item2;

    private User owner1;

    private Long ownerId1 = 1L;

    private Long ownerId2 = 2L;

    private User owner2;

    private final String xShareUserId = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;


    @BeforeEach
    void init() {
        owner1 = new User(1, "Olga", "Olga123@mail.ru");
        item1 = new Item(1, "Flowers", "Roses", true, owner1, new HashSet<>(), null);

        owner2 = new User(2, "Petya", "Petya1@mail.ru");
        item2 = new Item(3, "Карандаш", "Цветной", true, owner2, new HashSet<>(), null);
    }

    @Test
    void addItem_Valid() throws Exception {
        when(itemService.addItem(any(Item.class), anyLong())).thenReturn(item1);

        mockMvc.perform(post("/items")
                        .header(xShareUserId, ownerId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(item1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Flowers"))
                .andExpect(jsonPath("$.description").value("Roses"));
    }


    @Test
    void addItem_NotValid() throws Exception {
        ItemCreate create = new ItemCreate();
        create.setRequestId(4L);
        create.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header(xShareUserId, ownerId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(create)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(itemMapper.toItem(create, ownerId1), 1L);
    }


    @Test
    void updateItem_Ok() throws Exception {
        UpdateItemDto update = new UpdateItemDto();
        update.setOwner(owner2);
        update.setDescription("NewDescription");
        update.setAvailable(false);
        update.setName("UpdatedName");

        Item updatedItem = new Item(1, "UpdatedName", "NewDescription", false, owner2, new HashSet<>(),
               null);

        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header(xShareUserId, ownerId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.description").value("NewDescription"))
                .andExpect(jsonPath("$.available").value(false));
    }


    @Test
    void getItemById() throws Exception {
        ItemResponseWithBooking item = new ItemResponseWithBooking();
        ShortBookingDto booking = new ShortBookingDto(1L, 1L);
        item.setId(1);
        item.setName("Flowers");
        item.setDescription("Roses");
        item.setAvailable(true);
        item.setComments(new HashSet<>());
        item.setLastBooking(booking);

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header(xShareUserId, ownerId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Flowers"))
                .andExpect(jsonPath("$.description").value("Roses"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.lastBooking.id").value(1L));
    }

    @Test
    void getUserItems() throws Exception {
        ItemResponseWithBooking item = new ItemResponseWithBooking();
        ShortBookingDto booking = new ShortBookingDto(1L, 1L);
        item.setId(1);
        item.setName("Flowers");
        item.setDescription("Roses");
        item.setAvailable(true);
        item.setComments(new HashSet<>());
        item.setLastBooking(booking);

        List<ItemResponseWithBooking> items = new ArrayList<>();
        items.add(item);

        when(itemService.getUserItems(anyLong())).thenReturn(items);

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item);

        mockMvc.perform(get("/items")
                        .header(xShareUserId, ownerId1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    void searchItem() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(item2);

        when(itemService.searchByName(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Карандаш")
                        .header(xShareUserId, ownerId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    void addComment() throws Exception {
        CommentCreateDto comment = new CommentCreateDto();
        comment.setAuthor(new UserDto(1L, "Olga", "Olga555@mail.ru"));
        comment.setItem(new ItemDto(5L, "Декорации", 1L));
        comment.setText("Все ок");
        comment.setCreated(LocalDateTime.now());

        Comment responseComment = new Comment();
        responseComment.setItem(new Item(5L, "Декорации", "праздничные декорации", true, owner1, null, null));
        responseComment.setId(1L);
        responseComment.setAuthor(new User(1L, "Olga", "Olga555@mail.ru"));
        responseComment.setText("Все окей");
        responseComment.setCreated(LocalDateTime.now().withNano(0));

        when(itemService.addComment(anyLong(), anyLong(), any(Comment.class))).thenReturn(responseComment);

        mockMvc.perform(post("/items//{itemId}/comment", 1)
                        .header(xShareUserId, ownerId2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Все окей"))
                .andExpect(jsonPath("$.authorName").value("Olga"))
                .andExpect(jsonPath("$.created").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(responseComment.getCreated())));
    }
}