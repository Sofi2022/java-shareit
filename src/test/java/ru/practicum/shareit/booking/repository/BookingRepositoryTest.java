//package ru.practicum.shareit.booking.repository;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.booking.model.Status;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@DataJpaTest
//class BookingRepositoryTest {
//
//    @Autowired
//    private BookingRepository bookingRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ItemRepository itemRepository;
//
//    private Booking booking;
//
//    private User booker;
//
//    private User owner;
//
//    private Item item;
//
//    List<Booking> bookings;
//
//    private final long bookerId = 2L;
//
//    @BeforeEach
//    void init() {
//        LocalDateTime start = LocalDateTime.of(2023, 6, 26, 12, 30);
//        LocalDateTime end = LocalDateTime.of(2023, 6, 26, 18, 0);
//        owner = userRepository.save(new User(1L, "Olga", "Olga123@mail.ru"));
//        item = itemRepository.save(new Item(1L, "Flowers", "Roses", true, owner,
//                new HashSet<>(), null));
//        booker = userRepository.save(new User(2L, "Petya", "Petya1@mail.ru"));
//        booking =  bookingRepository.save(new Booking(1L, start, end, item, booker, Status.WAITING));
//        bookings = getBookings();
//    }
//
//    private Booking makeBooking(Long id, LocalDateTime start, LocalDateTime end, Item item, User booker, Status status) {
//        return new Booking(10L, start, end, item, booker, status);
//    }
//
//    private Booking makeBookingWithState(Long bookingId, LocalDateTime start, LocalDateTime end, Status status,
//                                         Item item, User booker) {
//        Booking booking2 = new Booking();
//        booking2.setId(bookingId);
//        booking2.setStart(start);
//        booking2.setEnd(end);
//        booking2.setItem(item);
//        booking2.setStatus(status);
//        booking2.setBooker(booker);
//        System.out.println("ITEMS: " + itemRepository.findAll());
//        return bookingRepository.save(booking2);
//    }
//
//
//    private List<Booking> getBookings() {
//        Item item2 = new Item(2L, "Платье", "Вечернее платье", true, owner, new HashSet<>(), null);
//        itemRepository.save(item2);
//        User user3 = new User(3L, "Вера", "Vera1998@ya.ru");
//        userRepository.save(user3);
//        User user4 = new User(4L, "Ксения", "Ksenya12@mail.ru");
//        userRepository.save(user4);
//
//        return List.of(
//                makeBookingWithState(2L, LocalDateTime.of(2023, 6, 26, 12, 30),
//                        LocalDateTime.of(2023, 6, 27, 12, 30),
//                        Status.WAITING, item2, user3),
//                makeBookingWithState(3L, LocalDateTime.of(2023, 2, 10, 12, 30),
//                        LocalDateTime.of(2023, 2, 11, 12, 30),
//                        Status.APPROVED, item2, user3),
//                makeBookingWithState(4L, LocalDateTime.of(2023, 6, 26, 12, 30),
//                        LocalDateTime.of(2023, 6, 26, 12, 30),
//                        Status.REJECTED, item2, user3),
//                makeBookingWithState(5L, LocalDateTime.of(2023, 2, 12, 12, 30),
//                        LocalDateTime.of(2023, 2, 14, 12, 30),
//                        Status.CANCELED, item2, user3),
//                makeBookingWithState(5L, LocalDateTime.of(2023, 2, 12, 12, 30),
//                        LocalDateTime.of(2023, 2, 14, 12, 30),
//                        Status.CANCELED, item2, user4)
//        );
//    }
//
//
//    @Test
//    void findById_WhenBookingFound() {
//        Optional<Booking> actualBooking = bookingRepository.findById(1L);
//
//        assertNotNull(actualBooking);
//        assertEquals(2, actualBooking.get().getBooker().getId());
//    }
//
//
//    @Test
//    void findById_WhenBookingNotFound() {
//        Optional<Booking> actualBooking = bookingRepository.findById(100L);
//
//        assertEquals(Optional.empty(), actualBooking);
//    }
//
//    @Test
//    void findBookingByBookerIdOrderByStartDesc() {
//        List<Booking> actualBookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId);
//
//        assertEquals(1, actualBookings.size());
//        assertEquals("WAITING", actualBookings.get(0).getStatus().toString());
//    }
//
//    @Test
//    void findBookingsByBookerIdOrderByStartDesc() {
//        PageRequest pageRequest = PageRequest.of(1, 2);
//        Page<Booking> actualBookings = bookingRepository.findBookingsByBookerIdOrderByStartDesc(pageRequest, 2L);
//        System.out.println("ACTUAL: " + actualBookings.getSize());
//
//        assertEquals(2, actualBookings.getSize());
//    }
//
//    @Test
//    void findAllByItem_OwnerIdOrderByStartDesc() {
//        PageRequest pageRequest = PageRequest.of(1, 3);
//        Page<Booking> actualBookings = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(pageRequest, 1L);
//
//        assertEquals(3, actualBookings.getSize());
//    }
//
//    @AfterEach
//    void clean() {
//        bookingRepository.deleteAll();
//    }
//
//    @Test
//    void findLastBookingsByItemIdOrderByStartDesc() {
//
//    }
//
//    @Test
//    void findNextBookingsByItemIdOrderByStartAsc() {
//    }
//
//    @Test
//    void findUserBookingsWaitingState() {
//    }
//
//    @Test
//    void findUserBookingsRejectedState() {
//    }
//
//    @Test
//    void findUserBookingsCurrentState() {
//    }
//
//    @Test
//    void findFutureByBooker() {
//    }
//
//    @Test
//    void findPastByBooker() {
//    }
//
//    @Test
//    void findAllByOwnerIdOrderByStartDesc() {
//    }
//
//    @Test
//    void findFutureByOwnerId() {
//    }
//
//    @Test
//    void findPastByOwnerId() {
//    }
//
//    @Test
//    void findCurrentByOwnerId() {
//    }
//
//    @Test
//    void findAllBookingsByOwnerIdAndStateIgnoreCase() {
//    }
//
//    @Test
//    void findBookingByBookerAndItemId() {
//    }
//}