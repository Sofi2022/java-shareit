package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl service;

    @Mock
    private BookingRepository repository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private long itemId = 5L;

    private long bookerId = 6L;

    private Booking booking;

    private User booker;

    private User owner;

    private Item item;


    private final long bookingId = 1L;

    private final String xShareUserId = "X-Sharer-User-Id";


    @BeforeEach
    void init(){
        LocalDateTime start = LocalDateTime.of(2023, 6, 26, 12, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 26, 18, 0);
        owner = new User(5, "Olga", "Olga123@mail.ru");
        item = new Item(5, "Flowers", "Roses", true, owner, new HashSet<>(), null);
        booker = new User(6, "Petya", "Petya1@mail.ru");
        booking = new Booking(1L, start, end, item, booker, Status.WAITING);
    }

    @Test
    void addBooking_Valid() {

        when(itemRepository.findById(itemId)).thenReturn(Optional.ofNullable(item));

        when(userRepository.findById(bookerId)).thenReturn(Optional.ofNullable(booker));

        Booking savedBooking = service.addBooking(booking, itemId, bookerId);

        when(repository.save(booking)).thenReturn(savedBooking);

        assertEquals(booking, savedBooking);
        verify(repository).save(booking);
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getBookingIds() {
    }

    @Test
    void update() {
    }

    @Test
    void approveOrRejectBooking() {
    }

    @Test
    void testGetBookingById() {
    }

    @Test
    void getAllUserBookingsById() {
    }

    @Test
    void getAllBookings() {
    }

    @Test
    void getAllUserBookings() {
    }

    @Test
    void getAllWithPage() {
    }

    @Test
    void getBookingsByState() {
    }

    @Test
    void getOwnerBookings() {
    }

    @Test
    void getAllByOwnerWithPage() {
    }

    @Test
    void getOwnerBookingsByState() {
    }
}