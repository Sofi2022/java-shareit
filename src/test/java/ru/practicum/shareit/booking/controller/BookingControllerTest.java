package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.reflect.Array.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private User user;

    private User owner;

    private Item item;

    private final long userId = 1L;

    private final long bookingId = 1L;

    private final String xShareUserId = "X-Sharer-User-Id";



    @BeforeEach
    void init(){
        LocalDateTime start = LocalDateTime.of(2023, 1, 26, 12, 30);
        LocalDateTime end = LocalDateTime.of(2023, 1, 26, 18, 0);
        owner = new User(1, "Olga", "Olga123@mail.ru");
        item = new Item(1, "Flowers", "Roses", true, owner, new HashSet<>(), null);
        user = new User(2, "Petya", "Petya1@mail.ru");
        booking = new Booking(1L, start, end, item, user, Status.WAITING);
    }


    @Test
    void addBooking_Valid() throws Exception {

        when(service.addBooking(any(), anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header(xShareUserId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(booking)))
                        .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                //.andExpect(jsonPath("$.start").value(booking.getStart()))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()));
                //.andExpect(jsonPath("$.item").value(booking.getItem()));
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

        verify(service, never()).addBooking(bookingMapper.toBooking(create), 0L, userId);
    }

    public static Stream<Arguments> prepareData() {
        User user2 = new User(2, "Маша", "Mari1998@ya.ru");
        Item item2 = new Item(2, "Декорации", "Декорации из шаров", true, user2, new HashSet<>(), null);
        Booking bookingRejected = new Booking(1L, LocalDateTime.of(2023, 2, 2, 12, 30, 0),
                LocalDateTime.of(2023, 2, 10, 12, 30), item2, user2, Status.REJECTED);

        User user3 = new User(3, "Сввета", "Sv1998@ya.ru");
        Item item3 = new Item(3, "Карандащ", "Цветной", true, user3, new HashSet<>(), null);
        Booking bookingApproved = new Booking(1L, LocalDateTime.of(2023, 2, 2, 12, 30),
                LocalDateTime.of(2023, 2, 10, 12, 30), item2, user2, Status.APPROVED);


        return Stream.of(
                Arguments.of("true", "APPROVED", bookingApproved),
                Arguments.of("false", "REJECTED", bookingRejected)
        );
    }


        @ParameterizedTest
        @MethodSource("prepareData")
    void updateBooking_Valid_With_Approve(String approved, String status, Booking booking) throws Exception {
        when(service.update(anyBoolean(), anyLong(), anyLong(), any())).thenReturn(booking);
        when(service.approveOrRejectBooking(anyLong(), anyBoolean(), anyLong())).thenReturn(booking);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                .param("approved", approved)
                .header(xShareUserId, userId))
            .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value(status));
        }

    @Test
    void updateBooking_Valid_WithOut_Approve() throws Exception {
        Booking updatedBooking = new Booking(1L, LocalDateTime.of(2023, 1, 27, 12, 30),
                LocalDateTime.of(2023, 1, 28, 18, 0), item, user, Status.CANCELED);

        when(service.update(anyBoolean(), anyLong(), anyLong(), any())).thenReturn(updatedBooking);

        UpdateBookingDto update = new UpdateBookingDto();
        update.setStart(LocalDateTime.of(2023, 1, 27, 12, 30));
        update.setEnd( LocalDateTime.of(2023, 1, 28, 18, 0));
        update.setStatus(Status.CANCELED);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(xShareUserId, bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }


    @Test
    void getBookingById_BookingFound() throws Exception { // booking = new Booking(1L, start, end, item, user, Status.WAITING);
        when(service.getBookingById(anyLong(), anyLong())).thenReturn(booking);

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1)
                        .header(xShareUserId, bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.start").value(booking.getStart()))
//                .andExpect(jsonPath("$.end").value(booking.getEnd()))
                //.andExpect(jsonPath("$.item").value(booking.getItem()))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()))
                .andExpect(jsonPath("$.status").value("WAITING"));

    }

    @Test
    void getBookingById_BookingNotFound_WithWrongBookingId() throws Exception {
        when(service.getBookingById(10L, 1L)).thenThrow(new NotFoundException("Такого букинга нет"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/10")
                        .header(xShareUserId, 1))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    @Test
    void getBookingById_BookingNotFound_WithWrongUserId() throws Exception {
        when(service.getBookingById(1L, 7L)).thenThrow(new NotFoundException("Такого букинга нет"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(xShareUserId, 7))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }



    @ParameterizedTest
    @MethodSource("dataSourceGetUsers")
    void getAllUserBookings(String state, Integer from, Integer size) throws Exception {
        when(service.getAllUserBookings(anyInt(), anyInt(), anyLong(), any(State.class))).thenReturn(List.of(booking));

        User user3 = new User(3, "Вера", "Vera1998@ya.ru");
        Item item3 = new Item(3, "Платье", "Вечернее платье", true, user3, new HashSet<>(), null);
        Booking booking2 = new Booking(1L, LocalDateTime.of(2023, 2, 2, 12, 30, 0),
                LocalDateTime.of(2023, 2, 10, 12, 30), item3, user3, Status.REJECTED);

//        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
//                        .param("state", state)
//                        .header(xShareUserId, userId))
//                .andExpect(status().isOk())



    }

//    public static Stream<Arguments> dataSourceGetUsers() {
//        return Stream.of(
//                Arguments.of("ALL", )
//        )
//    }


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
        item.setId(++id);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return item;
    }

    private User createUser(String name, String email){
        long id = 1;
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}