package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemCreateRequest;
import ru.practicum.shareit.request.dto.ShortItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({ItemRequestController.class, BookingMapper.class, ItemMapper.class, CommentMapper.class, ItemRequestMapper.class})
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookingMapper bookingMapper;

    @MockBean
    ItemRequestImpl service;

    @Spy
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);

    private final String xShareUserId = "X-Sharer-User-Id";

    private User requester;

    private Long userId = 1L;

    private ItemCreateRequest itemCreateRequest;

    private ItemRequest request;

    private ItemRequest request2;

    private Item item1;

    private User owner;

    @BeforeEach
    void init() {
        Set<Item> items = new HashSet<>();
        items.add(item1);
        requester = new User(1L, "Petya", "Petya123@mail.ru");
        itemCreateRequest = new ItemCreateRequest("Description", requester, LocalDateTime.now().withNano(0));
        owner = new User(2L, "Misha", "Misha123@mail.ru");
        item1 = new Item(1L, "Item1", "Descr1", true, owner, null, null);
        request = new ItemRequest(1, "Description", requester, LocalDateTime.now()
                .withNano(0), items);
        request2 = new ItemRequest(2, "Description2", requester, LocalDateTime.now()
                .withNano(0), items);
    }

    @Test
    void addRequest() throws Exception {
        when(service.addRequest(any(), any(ItemRequest.class))).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header(xShareUserId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.created").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(request.getCreated())))
                .andExpect(jsonPath("$.items.length()").value(1));
    }


    @Test
    void getRequests() throws Exception {
        List<ItemResponse> itemResponses = new ArrayList<>();
        itemResponses.add(new ItemResponse(1L, "Item1", "Descr1", true, 1L));

        List<ShortItemRequest> requests = new ArrayList<>();
        ShortItemRequest req1 = new ShortItemRequest(1, "Description", LocalDateTime.now()
                .withNano(0), itemResponses);
        requests.add(req1);

        when(service.getItemRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(xShareUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllRequests_WithPage() throws Exception {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(request);
        when(service.getAllWithPage(any(), anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(xShareUserId, userId)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    void getAllUserBookings_WithOutPage() throws Exception {
        List<ItemResponse> itemResponses = new ArrayList<>();
        itemResponses.add(new ItemResponse(1L, "Item1", "Descr1", true, 1L));
        itemResponses.add(new ItemResponse(2L, "Item2", "Descr2", true, 1L));

        ShortItemRequest request1 = new ShortItemRequest(1, "Description", LocalDateTime.now()
                .withNano(0), itemResponses);

        when(service.getAllRequests(userId)).thenReturn(List.of(request1));

        mockMvc.perform(get("/requests/all")
                        .header(xShareUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequestById() throws Exception {
        List<ItemResponse> itemResponses = new ArrayList<>();
        itemResponses.add(new ItemResponse(1L, "Item1", "Descr1", true, 1L));
        itemResponses.add(new ItemResponse(2L, "Item2", "Descr2", true, 1L));

        ShortItemRequest request1 = new ShortItemRequest(1, "Description", LocalDateTime.now()
                .withNano(0), itemResponses);
        when(service.getById(anyLong(), anyLong())).thenReturn(request1);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header(xShareUserId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.items.length()").value(2));
    }
}