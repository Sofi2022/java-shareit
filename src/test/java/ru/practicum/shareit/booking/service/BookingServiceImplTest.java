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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        booker = new User(3, "Petya", "Petya1@mail.ru");
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
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        Booking actualBooking = service.getBookingById(bookingId, userId);
        assertEquals(booking, actualBooking);
    }


    @Test
    void getBookingById_WhenBookingNotFound(){
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingById(bookingId, userId), "Такого букинга нет "
                + bookingId);
    }


    @Test
    void getBookingById_ThrowsException(){
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingById(bookingId, userId), userId +
                " не является владельцем вещи/автором бронирования, к которому относится бронирование");
    }

    @Test
    void update_WhenBookingFound_isApproveTrue() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        service.update(true, bookingId, 5L, booking);
        verify(bookingRepository).save(booking);

        Booking savedBooking = service.getBookingById(bookingId, userId);
        assertEquals(Status.APPROVED, savedBooking.getStatus());
    }


    @Test
    void update_WhenBookingFound_isApproveFalse() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));


        service.update(false, bookingId, 5L, booking);
        verify(bookingRepository).save(booking);

        Booking savedBooking = service.getBookingById(bookingId, userId);
        assertEquals(Status.REJECTED, savedBooking.getStatus());
    }

    @Test
    void update_WhenIsApprove_Null(){
        LocalDateTime updateStart = LocalDateTime.of(2023, 7, 27, 13, 40);
        LocalDateTime updateEnd = LocalDateTime.of(2023, 8, 27, 14, 50);

        Item updateItem = new Item(10L, "Книга", "Учебное пособие по Java",
                true, owner, null, null);
        User updateBooker = new User(6L, "Катя", "Katya567@mail.ru");
        Booking update = new Booking(1L, updateStart, updateEnd, updateItem, updateBooker, Status.REJECTED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        service.update(null, bookingId, 5L, update);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(updateStart, savedBooking.getStart());
        assertEquals(updateEnd, savedBooking.getEnd());
        assertEquals(updateItem, savedBooking.getItem());
        assertEquals(updateBooker, savedBooking.getBooker());
        assertEquals(Status.REJECTED, savedBooking.getStatus());
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


//    @Test
//    void approveOrRejectBooking() {
//        Booking actualBooking = makeBooking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker,
//                Status.WAITING);
//
//        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(actualBooking));
//        service.update(true, 1L, 6L, null);
//        verify(bookingRepository, times(1)).save(actualBooking);
//        verify(bookingRepository).save(bookingArgumentCaptor.capture());
//        Booking savedBooking = bookingArgumentCaptor.getValue();
//        assertEquals(Status.APPROVED, savedBooking.getStatus());
//    }


    @Test
    void approveOrRejectBooking_TrowsException(){
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingById(bookingId, userId));
    }


    @Test
    void getAllUserBookingsById() {
        List<Booking> bookings = List.of(booking, makeBooking(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                new Item(7L, "коммпьютер", "ноутбук", true, owner, null, null), booker, Status.WAITING));

        when(userRepository.findById(6L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(6L)).thenReturn(bookings);

        List<Booking> actualList = service.getAllUserBookingsById(6L);
        assertEquals(actualList, bookings);
    }


    @Test
    void getAllBookings() {
        List<Booking> bookings = List.of(booking, makeBooking(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                new Item(7L, "коммпьютер", "ноутбук", true, owner, null, null),
                booker, Status.WAITING));
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> actualList = service.getAllBookings();
        assertEquals(actualList, bookings);
    }

    @Test
    void getAllUserBookings_WithPage() {
        List<Booking> bookings = List.of(booking, makeBooking(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                new Item(7L, "коммпьютер", "ноутбук", true, owner, null, null),
                booker, Status.WAITING));
        PageImpl<Booking> page = new PageImpl<>(bookings);


        when(bookingRepository.findBookingsByBookerIdOrderByStartDesc(any(), anyLong())).thenReturn(page);
        List<Booking> actualBookings = service.getAllUserBookings(1, 2, userId, State.ALL);
        assertEquals(bookings, actualBookings);
    }


    @ParameterizedTest
    @MethodSource("sourceWithState")
    void getAllUserBookings_WithOutPage(String state, List<Booking> bookings){
        when(bookingRepository.findBookingsByBookerIdOrderByStartDesc(any(), anyLong())).thenReturn(Page.empty());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        //when(bookingRepository.findBookingByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);
    }

    public static Stream<Arguments> sourceWithState() {
        List<Booking> bookings = getBookings();
        return Stream.of(
                Arguments.of("ALL", bookings),
                Arguments.of("FUTURE", List.of(getBookings().get(0), getBookings().get(2))),
                Arguments.of("PAST", List.of(getBookings().get(1))),
                Arguments.of("WAITING", List.of(getBookings().get(0))),
                Arguments.of("REJECTED", List.of(getBookings().get(2))),
                Arguments.of("CURRENT", List.of(getBookings().get(3)))
        );
    }

    private static Booking makeBookingWithState(LocalDateTime start, LocalDateTime end, Status status, Item item, User user){
        Booking booking2 = new Booking(1L, start,
                end, item, user, null);
        booking2.setStart(start);
        booking2.setEnd(end);
        booking2.setStatus(status);
        return booking2;
    }


    private static List<Booking> getBookings(){
        User user3 = new User(3, "Вера", "Vera1998@ya.ru");
        Item item3 = new Item(3, "Платье", "Вечернее платье", true, user3, new HashSet<>(), null);

        return List.of(
                makeBookingWithState(LocalDateTime.of(2023, 6, 26, 12, 30),
                        LocalDateTime.of(2023, 6, 27, 12, 30),
                        Status.WAITING, item3, user3),
                makeBookingWithState(LocalDateTime.of(2023, 2, 10, 12, 30),
                        LocalDateTime.of(2023, 2, 11, 12, 30),
                        Status.APPROVED, item3, user3),
                makeBookingWithState(LocalDateTime.of(2023, 6, 26, 12, 30),
                        LocalDateTime.of(2023, 6, 26, 12, 30),
                        Status.REJECTED, item3, user3),
                makeBookingWithState(LocalDateTime.of(2023, 2, 12, 12, 30),
                        LocalDateTime.of(2023, 2, 14, 12, 30),
                        Status.CANCELED, item3, user3)
        );
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