package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    private final long itemId = 5L;

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
    void init() {
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
    void addBooking_WitPastEnd() {
        booking.setEnd(LocalDateTime.of(2020, 6, 26, 12, 30));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () -> service.addBooking(booking, itemId, userId),
                "Завершение бронирования не может быть в прошлом времени");
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void addBooking_WitEarlyEnd() {
        booking.setStart(LocalDateTime.of(2023, 6, 27, 12, 30));
        booking.setEnd(LocalDateTime.of(2023, 6, 26, 12, 30));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () -> service.addBooking(booking, itemId, userId),
                "Завершение бронирования не может быть раньше его начала");
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void addBooking_WitNotAvailbleItem() {
        item.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        assertThrows(ValidationException.class, () -> service.addBooking(booking, itemId, userId),
                "Эта вещь недоступна для бронирования " + item.getId());
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void addBooking_WhenUserOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        assertThrows(NotFoundException.class, () -> service.addBooking(booking, itemId, 5L),
                "Нельзя забронировать свою вещь " + itemId);
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void getBookingById_WhenBookingFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        Booking actualBooking = service.getBookingById(bookingId, userId);
        assertEquals(booking, actualBooking);
    }

    // bookingId = 1L; userId = 3L

    @Test
    void getBookingById_WhenBookingFound_() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        Booking actualBooking = service.getBookingById(bookingId, 5L);
        assertEquals(booking, actualBooking);
    }

    @Test
    void getBookingById_WhenBookingNotFound() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getBookingById(bookingId, userId), "Такого букинга нет "
                + bookingId);
    }


    @Test
    void getBookingById_ThrowsException() {
        long wrongId = 19;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThrows(NotFoundException.class, () -> service.getBookingById(bookingId, wrongId), wrongId +
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
    void update_WhenBookingNotFound_isApproveNull() {
        Long wrongId = 15L;

        assertThrows(NotFoundException.class, () -> service.update(null, wrongId, userId, booking),
                "Такого букинга нет " + wrongId);
    }


    @Test
    void update_ThrowsException_isApproveNull() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> service.update(null, bookingId, 15L, booking),
                "Вы не являетесь владельцем данной вещи");
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
    void update_WhenIsApprove_Null() {
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


    @Test
    void update_WhenIsApprove_Null_StartNull() {
        User updateBooker = new User(6L, "Катя", "Katya567@mail.ru");
        Booking update = new Booking(1L, null, null, null, updateBooker, null);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        service.update(null, bookingId, 5L, update);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(LocalDateTime.of(2023, 6, 26, 12, 30), savedBooking.getStart());
        assertEquals(LocalDateTime.of(2023, 6, 26, 18, 0), savedBooking.getEnd());
        assertEquals(item, savedBooking.getItem());
        assertEquals(updateBooker, savedBooking.getBooker());
        assertEquals(Status.WAITING, savedBooking.getStatus());
    }


    @Test
    void update_WhenIsApprove_OnlyItemUpdated() {
        Item updateItem = new Item(20L, "Item", "Description", true, owner, null, null);
        Booking update = new Booking(1L, null, null, updateItem, null, null);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        service.update(null, bookingId, 5L, update);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(LocalDateTime.of(2023, 6, 26, 12, 30), savedBooking.getStart());
        assertEquals(LocalDateTime.of(2023, 6, 26, 18, 0), savedBooking.getEnd());
        assertEquals(updateItem, savedBooking.getItem());
        assertEquals(booker, savedBooking.getBooker());
        assertEquals(Status.WAITING, savedBooking.getStatus());
    }


    private static Booking makeBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
        return new Booking(10L, start, end, item, booker, status);
    }

    @Test
    void updateBooking_WithException() {
        Booking actualBooking = makeBooking(LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker,
                Status.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(actualBooking));
        assertThrows(NotFoundException.class, () -> service.approveOrRejectBooking(bookingId, true, 10L),
                "Вы не являетесь владельцем данной вещи");
    }


    @Test
    void approveOrRejectBooking_TrowsException() {
        Long wrongId = 15L;
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.approveOrRejectBooking(wrongId, true, userId),
                "Такого букинга нет " + bookingId);
    }


    @Test
    void approveOrRejectBooking_WhenStatusApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        booking.setStatus(Status.APPROVED);

        assertThrows(ValidationException.class, () -> service.approveOrRejectBooking(bookingId, true, 5L),
                "Статус уже подтвержден " + booking.getStatus());
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


        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findBookingsByBookerIdOrderByStartDesc(any(), anyLong())).thenReturn(page);
        List<Booking> actualBookings = service.getAllUserBookings(1, 2, userId, State.ALL);
        assertEquals(bookings, actualBookings);
    }


    @Test
    void getBookingsByStateTest() {
        List<Booking> bookings = getBookings();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(userId)).thenReturn(bookings);
        List<Booking> actualBookings = service.getAllUserBookings(0, 0, userId, State.ALL);
        assertEquals(bookings, actualBookings);

        when(bookingRepository.findFutureByBooker(userId, LocalDateTime.now().withNano(0)))
                .thenReturn(List.of(getBookings().get(0), getBookings().get(2)));
        service.getAllUserBookings(0, 0, userId, State.FUTURE);
        verify(bookingRepository, times(1)).findFutureByBooker(anyLong(), any());

        List<Booking> pastBookings = List.of(getBookings().get(1), getBookings().get(3));
        when(bookingRepository.findPastByBooker(userId, LocalDateTime.now().withNano(0))).thenReturn(pastBookings);
        service.getBookingsByState(State.PAST, userId);
        verify(bookingRepository, times(1)).findPastByBooker(anyLong(), any());

        List<Booking> waitingBookings = List.of(getBookings().get(0));
        when(bookingRepository.findUserBookingsWaitingState(userId)).thenReturn(waitingBookings);
        service.getBookingsByState(State.WAITING, userId);
        verify(bookingRepository, times(1)).findUserBookingsWaitingState(userId);

        List<Booking> rejectedBookings = List.of(getBookings().get(2));
        when(bookingRepository.findUserBookingsRejectedState(userId)).thenReturn(rejectedBookings);
        service.getBookingsByState(State.REJECTED, userId);
        verify(bookingRepository, times(1)).findUserBookingsRejectedState(userId);

        Booking booking1 = makeBookingWithState(LocalDateTime.of(2023, 2, 12, 12, 30),
                LocalDateTime.of(2023, 6, 14, 12, 30),
                Status.CANCELED, item, booker);
        List<Booking> currentBookings = List.of(booking1);
        when(bookingRepository.findUserBookingsCurrentState(userId, LocalDateTime.now().withNano(0)))
                .thenReturn(currentBookings);
        service.getBookingsByState(State.CURRENT, userId);
        verify(bookingRepository, times(2)).findUserBookingsCurrentState(userId,
                LocalDateTime.now().withNano(0));
    }


    @Test
    void getBookingsByStateTest_Exception() {
        assertThrows(IllegalArgumentException.class, () -> service.getBookingsByState(State.valueOf("WRONG"), userId),
                "Unknown state");
    }


    private static Booking makeBookingWithState(LocalDateTime start, LocalDateTime end, Status status, Item item, User user) {
        Booking booking2 = new Booking(1L, start,
                end, item, user, null);
        booking2.setStart(start);
        booking2.setEnd(end);
        booking2.setStatus(status);
        return booking2;
    }


    private static List<Booking> getBookings() {
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
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<Booking> bookings = List.of(booking, makeBooking(LocalDateTime.now(), LocalDateTime.now().plusDays(2),
                new Item(7L, "коммпьютер", "ноутбук", true, owner, null, null),
                booker, Status.WAITING));
        PageImpl<Booking> page = new PageImpl<>(bookings);

        when(bookingRepository.findBookingsByBookerIdOrderByStartDesc(any(), anyLong())).thenReturn(page);
        List<Booking> actualBookings = service.getAllWithPage(pageRequest, userId);
        assertEquals(bookings, actualBookings);
    }


    @Test
    void getOwnerBookings_WithOutPage() {
        List<Item> items = List.of(item, new Item(10L, "Книга", "Учебное пособие по Java",
                true, owner, null, null), new Item(7L, "коммпьютер", "ноутбук",
                true, owner, null, null), new Item(16L, "Отпариватель", "Отпариватель для одежды",
                true, new User(13L, "Kostya", "Kostya123@mail.ru"), null, null));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findAll()).thenReturn(items);

        List<Booking> bookings = getBookings();
        when(bookingRepository.findAllByOwnerIdOrderByStartDesc(anyLong())).thenReturn(bookings);
        service.getOwnerBookings(0, 0, 5L, State.ALL);
        verify(bookingRepository, times(1)).findAllByOwnerIdOrderByStartDesc(any());

        when(bookingRepository.findFutureByOwnerId(anyLong(), any())).thenReturn(List.of(getBookings().get(0),
                getBookings().get(2)));
        service.getOwnerBookings(0, 0, 5L, State.FUTURE);
        verify(bookingRepository, times(1)).findFutureByOwnerId(anyLong(), any());

        when(bookingRepository.findPastByOwnerId(anyLong(), any())).thenReturn(List.of(getBookings().get(1)));
        service.getOwnerBookings(0, 0, 5L, State.PAST);
        verify(bookingRepository, times(1)).findPastByOwnerId(anyLong(), any());

        when(bookingRepository.findAllBookingsByOwnerIdAndStateIgnoreCase(any(Status.class), any())).thenReturn(
                List.of(getBookings().get(0)));
        service.getOwnerBookings(0, 0, 5L, State.WAITING);

        when(bookingRepository.findAllBookingsByOwnerIdAndStateIgnoreCase(any(Status.class), any())).thenReturn(
                List.of(getBookings().get(2)));
        service.getOwnerBookings(0, 0, 5L, State.REJECTED);
        verify(bookingRepository, times(2)).findAllBookingsByOwnerIdAndStateIgnoreCase(any(Status.class),
                any());

        when(bookingRepository.findCurrentByOwnerId(anyLong(), any())).thenReturn(List.of(getBookings().get(3)));
        service.getOwnerBookings(0, 0, 5L, State.CURRENT);
        verify(bookingRepository, times(1)).findCurrentByOwnerId(anyLong(), any());
    }


    @Test
    void getOwnerBookings_Exception() {
        long wrongId = 20L;
        List<Item> items = List.of(item, new Item(10L, "Книга", "Учебное пособие по Java",
                true, owner, null, null), new Item(7L, "коммпьютер", "ноутбук",
                true, owner, null, null), new Item(16L, "Отпариватель", "Отпариватель для одежды",
                true, new User(13L, "Kostya", "Kostya123@mail.ru"), null, null));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findAll()).thenReturn(items);

        assertThrows(NotFoundException.class, () -> service.getOwnerBookings(null, null, wrongId, State.ALL),
                wrongId + " не является владельцем");
    }


    @Test
    void getAllByOwnerWithPage() {
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<Booking> bookings = getBookings();
        PageImpl<Booking> page = new PageImpl<>(bookings);

        when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(pageRequest, 5L)).thenReturn(page);

        List<Booking> actualPage = service.getAllByOwnerWithPage(pageRequest, 5L);

        assertEquals(bookings, actualPage);
    }

    @Test
    void getOwnerBookingsByState_Exception() {
        assertThrows(IllegalArgumentException.class, () -> service.getOwnerBookingsByState(State.valueOf("WRONG"),
                        userId),
                "Unknown state");
    }


    @Test
    void getOwnerBookingsByState_WithPage() {
        List<Booking> bookings = getBookings();
        PageImpl<Booking> page = new PageImpl<>(bookings);

        List<Item> items = List.of(item, new Item(10L, "Книга", "Учебное пособие по Java",
                true, owner, null, null), new Item(7L, "коммпьютер", "ноутбук",
                true, owner, null, null), new Item(16L, "Отпариватель", "Отпариватель для одежды",
                true, new User(13L, "Kostya", "Kostya123@mail.ru"), null, null));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findAll()).thenReturn(items);
        when(bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(any(PageRequest.class), anyLong())).thenReturn(page);

        List<Booking> actualBookings = service.getOwnerBookings(2, 1, 5L, State.ALL);
        assertEquals(bookings, actualBookings);
    }
}