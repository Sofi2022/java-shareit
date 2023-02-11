package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AlreadyExists;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl service;

    @Mock
    private BookingRepository bookingRepository;

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

    private final long userId = 3L;

    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;

    private final long bookingId = 1L;

    private final String xShareUserId = "X-Sharer-User-Id";




    @BeforeEach
    void init(){
        LocalDateTime start = LocalDateTime.of(2023, 6, 26, 12, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 26, 18, 0);
        owner = new User(5, "Olga", "Olga123@mail.ru");
        userRepository.save(owner);
        item = new Item(5, "Flowers", "Roses", true, owner, new HashSet<>(), null);
        booker = new User(6, "Petya", "Petya1@mail.ru");
        userRepository.save(booker);
        booking = new Booking(1L, start, end, item, booker, Status.WAITING);
    }

    @Test
    void addBooking_Valid() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking actual = service.addBooking(booking, itemId, userId);

        assertEquals(booking, actual);
        verify(bookingRepository, times(1)).save(booking);
    }


    @Test
    void addBooking_NotValid() {
        booking.setStart(LocalDateTime.of(2020, 6, 26, 12, 30));
        booking.setEnd(LocalDateTime.of(2022, 6, 26, 12, 30));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> service.addBooking(booking, itemId, userId));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getBookingById_WhenBookingFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking actualBooking = service.getBookingById(bookingId);
        assertEquals(booking, actualBooking);
    }


    @Test
    void getBookingById_WhenBookingNotFound(){
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingById(bookingId));
    }


    @Test
    void update_WhenBookingFound_AndApproveNotNull() {
        Booking bookingForUpdate = new Booking();
        bookingForUpdate.setStatus(Status.REJECTED);
        bookingForUpdate.setStart(LocalDateTime.of(2023, 7, 27, 13, 30));

        Booking booking1 = new Booking(1L, LocalDateTime.of(2023, 7, 27, 13, 30),
                LocalDateTime.of(2023, 6, 26, 18, 0), item, booker, Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        service.update(null, bookingId, 5L, bookingForUpdate);
        verify(bookingRepository).save(bookingForUpdate);

        Booking savedBooking = service.getBookingById(bookingId);
        assertEquals(booking1, savedBooking);
    }

    @ParameterizedTest
    @MethodSource("sourceForUpdateBooking")
    void update_IsApprove_WhenIsApproveNull(Booking booking){
        Booking actualBooking = makeBooking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), null, new User(6, "Petya", "Petya1@mail.ru"),
        Status.APPROVED);

        when(bookingRepository.getById(anyLong())).thenReturn(actualBooking);
        service.update(null, 1L, 6L, booking);
        verify(bookingRepository, times(1)).save(actualBooking);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        //assertEquals();
    }

    public static Stream<Arguments> sourceForUpdateBooking() {
        LocalDateTime time = LocalDateTime.of(2023, 6, 26, 12, 30);
        return Stream.of(
                Arguments.of(makeBooking(time, null, null, null, null)),
                Arguments.of(makeBooking(null, time, null, null, null)),
                Arguments.of(makeBooking(null, null, new Item(5, "Flowers", "Roses",
                        true, null, new HashSet<>(), null), null, null)),
                Arguments.of(makeBooking(null, null, null, new User(6, "Petya", "Petya1@mail.ru"), null)),
                Arguments.of(makeBooking(null, null, null, null, Status.CANCELED))
        );
    }

    private static Booking makeBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, Status status){
        return new Booking(10L, start, end, item, booker, status);
    }

    @Test
    void updateBooking_WithException(){
        Booking actualBooking = makeBooking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker,
                Status.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(actualBooking));
        assertThrows(ValidationException.class, () -> service.update(null, 1L, 7L, null),
                "Вы не являетесь владельцем данной вещи");
    }


    @Test
    void approveOrRejectBooking() {
        Booking actualBooking = makeBooking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker,
                Status.WAITING);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(actualBooking));
        service.update(true, 5L, 6L, null);
        verify(bookingRepository, times(1)).save(actualBooking);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertEquals(Status.APPROVED, savedBooking.getStatus());

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