package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest({BookingController.class, BookingMapper.class, ItemMapper.class, CommentMapper.class})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookingMapper bookingMapper;

    @MockBean
    private BookingServiceImpl service;

    private Booking booking;

    private final long userId = 1L;

    private final String xShareUserId = "X-Sharer-User-Id";

    @BeforeEach
    void init(){
        var start = LocalDateTime.of(2023, 1, 26, 12, 30);
        // LocalDateTime.now().plusDays(-1);
        var end = LocalDateTime.of(2023, 1, 26, 18, 00);
        //LocalDateTime.now().plusDays(1);
        Item item = createItem("Flowers", "Roses", true, createUser("Olga", "Olga123@mail.ru"));
        User user = createUser("Petya", "Petya1@mail.ru");
        booking = new Booking(1L, start, end, item, user, Status.WAITING);
    }

//    @AfterEach
//    void clean(){
//
//    }



    @Test
    void addBooking_Valid() throws Exception {

        when(service.addBooking(any(), anyLong(), anyLong())).thenReturn(booking);

        String response = mockMvc.perform(post("/bookings")
                        .header(xShareUserId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(booking)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        System.out.println("Response " + response);

        assertEquals(mapper.writeValueAsString(bookingMapper.toBookingDto(booking)), response);
    }

    @Test
    void addBooking_Not_Valid() throws Exception {
        BookingCreateRequest create = new BookingCreateRequest();
        create.setItemId(0);
        create.setStart(null);
        create.setEnd(null);

        mockMvc.perform(post("/bookings")
                .header(xShareUserId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(create)))
                .andExpect(status().isBadRequest());

        //verify(service, never()).addBooking(bookingMapper.toBooking(create), 0L, userId);
    }


        @Test
    void updateBooking_NotValidReturnedBadRequest() {


    }

    @Test
    void getBookingById() {
    }

    @Test
    void getAllUserBookings() {
    }

    @Test
    void getOwnerBooking() {
    }

    private String getContentFromFile(final String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(),
                    StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new RuntimeException("Unable to open file", exception);
        }
    }

    private Item createItem(String name, String description, boolean available, User owner){
        long id = 1;
        Item item = new Item();
        item.setId(id++);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }

    private User createUser(String name, String email){
        long id = 1;
        User user = new User();
        user.setId(id++);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}