package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

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
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(booking.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(booking.getEnd())))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()))
                .andExpect(jsonPath("$.item.id").value(1));
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

        when(service.update(any(), anyLong(), anyLong(), any())).thenReturn(updatedBooking);

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
                .andExpect(jsonPath("$.status").value("CANCELED"))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(updatedBooking.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(updatedBooking.getEnd())))
                .andExpect(jsonPath("$.booker").value(updatedBooking.getBooker()))
                .andExpect(jsonPath("$.item.id").value(1));
    }


    @Test
    void getBookingById_BookingFound() throws Exception {
        when(service.getBookingById(anyLong(), anyLong())).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1)
                        .header(xShareUserId, bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(booking.getStart())))
                .andExpect(jsonPath("$.end").value(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .format(booking.getEnd())))
                .andExpect(jsonPath("$.item.id").value(1));

    }

    @Test
    void getBookingById_BookingNotFound_WithWrongBookingId() throws Exception {
        when(service.getBookingById(10L, 1L)).thenThrow(new NotFoundException("Такого букинга нет"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/10")
                        .header(xShareUserId, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingById_BookingNotFound_WithWrongUserId() throws Exception {
        when(service.getBookingById(1L, 7L)).thenThrow(new NotFoundException("Такого букинга нет"));

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(xShareUserId, 7))
                .andExpect(status().isNotFound());
    }


    @ParameterizedTest
    @MethodSource("dataSourceGetUsersBookings")
    void getAllUserBookings(String state, List<Booking> bookings, int listSize) throws Exception {
        when(service.getAllUserBookings(any(), any(), any(), any())).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .param("state", state)
                        .header(xShareUserId, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(listSize));
    }

    public static Stream<Arguments> dataSourceGetUsersBookings() {
        return Stream.of(
                Arguments.of("ALL", getBookings(), 4),
                Arguments.of(null, getBookings(), 4),
                Arguments.of("REJECTED", List.of(getBookings().get(2)), 1),
                Arguments.of("WAITING", List.of(getBookings().get(0)), 1),
                Arguments.of("PAST", List.of(getBookings().get(1)), 1),
                Arguments.of("CURRENT", List.of(getBookings().get(3)), 1),
                Arguments.of("FUTURE", List.of(getBookings().get(0)), 1)
        );
    }

    @ParameterizedTest
    @MethodSource("dataSourceGetUsersWithPage")
    void getAllUserBookingsWithPage(String from, String size, List<Booking> bookings, int listSize) throws Exception {
        when(service.getAllUserBookings(any(), any(), any(), any())).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                .param("from",from)
                .param("size", size)
                .header(xShareUserId, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(listSize));
    }


    public static Stream<Arguments> dataSourceGetUsersWithPage() {
        List<Booking> bookings = getBookings();
        return Stream.of(
                Arguments.of("0", "1", List.of(bookings.get(0)), 1),
                Arguments.of("1", "2", List.of(bookings.get(0), bookings.get(2)), 2),
                Arguments.of("2", "3", List.of(bookings.get(0), bookings.get(2), bookings.get(3)), 3)
        );
    }


    @ParameterizedTest
    @MethodSource("dataSourceGetUsersBookings")
    void getAllOwnerBookings(String state, List<Booking> bookings, int listSize) throws Exception {
        when(service.getOwnerBookings(any(), any(), any(), any())).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .param("state", state)
                        .header(xShareUserId, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(listSize));
    }


    @ParameterizedTest
    @MethodSource("dataSourceGetUsersWithPage")
    void getAllOwnerBookings_WithPage(String from, String size, List<Booking> bookings, int listSize) throws Exception {
        when(service.getOwnerBookings(any(), any(), any(), any())).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .param("from",from)
                        .param("size", size)
                        .header(xShareUserId, 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(listSize));
    }


    private String getContentFromFile(final String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(),
                    StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new RuntimeException("Unable to open file", exception);
        }
    }


    private static Booking makeBookingWithState(Status status, Item item, User user){
        Booking booking2 = new Booking(1L, LocalDateTime.of(2023, 2, 2, 12, 30, 0),
                LocalDateTime.of(2023, 2, 10, 12, 30), item, user, null);
        booking2.setStatus(status);
        return booking2;
    }

    private static List<Booking> getBookings(){
        User user3 = new User(3, "Вера", "Vera1998@ya.ru");
        Item item3 = new Item(3, "Платье", "Вечернее платье", true, user3, new HashSet<>(), null);

        return List.of(
                makeBookingWithState(Status.WAITING, item3, user3),
                makeBookingWithState(Status.APPROVED, item3, user3),
                makeBookingWithState(Status.REJECTED, item3, user3),
                makeBookingWithState(Status.CANCELED, item3, user3)
        );
    }
}