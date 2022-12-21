package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;


    void validate(Booking booking, Long itemId) {
        itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало бронирования не может быть в прошлом времени");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Завершение бронирования не может быть в прошлом времени");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Завершение бронирования не может быть раньше его начала");
        }
        Item item = itemRepository.getById(itemId);
        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь недоступна для бронирования " + item.getId());
        }
    }

    @Override
    public Booking addBooking(Booking booking, Long itemId, Long userId) {
        validate(booking, itemId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId));

        Item item = itemRepository.getById(itemId);
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Нельзя забронировать свою вещь " + itemId);
        }
        User booker = userRepository.getById(userId);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return getBookingById(booking.getId());
    }

    @Override
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Такого букинга нет "
                + bookingId));
    }

    @Override
    public List<Long> getBookingIds() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(Booking::getId).collect(Collectors.toList());
    }

    @Override
    public Booking approveOrRejectBooking(Long bookingId, Boolean isApprove, Long userid) {
        Booking booking = getBookingById(bookingId);
        if (booking.getItem().getOwner().getId() == userid) {
            if (isApprove) {
                if (booking.getStatus() == Status.APPROVED) {
                    throw new ValidationException("Статус уже подтвержден " + booking.getStatus());
                }
                booking.setStatus(Status.APPROVED);
                bookingRepository.save(booking);
            } else {
                booking.setStatus(Status.REJECTED);
                bookingRepository.save(booking);
            }
            bookingRepository.save(booking);
            return booking;
        } else {
            throw new NotFoundException("Вы не являетесь владельцем данной вещи");
        }
    }

    @Override
    public Booking update(Long bookingId, Long userId, Booking booking) {
        if (booking.getItem().getOwner().getId() == userId) {
            if (booking.getStart() != null) {
                booking.setStart(booking.getStart());
            }
            if (booking.getEnd() != null) {
                booking.setEnd(booking.getEnd());
            }
            if (booking.getStatus() != null) {
                booking.setStatus(booking.getStatus());
            }
            if (booking.getItem() != null) {
                booking.setItem(booking.getItem());
            }
            if (booking.getBooker() != null) {
                booking.setBooker(booking.getBooker());
            }
            bookingRepository.save(booking);
            return getBookingById(bookingId);
        } else {
            throw new ValidationException("Вы не являетесь владельцем данной вещи");
        }
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Такого букинга нет "
                + bookingId));
        valdateUser(userId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return booking;
        } else {
            throw new NotFoundException(userId + " не является владельцем вещи/автором бронирования, " +
                    "к которому относится бронирование");
        }
    }

    @Override
    public List<Booking> getAllUserBookings(Long userId) {
        valdateUser(userId);
        List<Booking> result = bookingRepository.findBookingByBookerIdOrderByStartDesc(userId);
        return result;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingsByState(State state, Long userId) {
        valdateUser(userId);
        switch (state) {
            case ALL:
                return bookingRepository.findBookingByBookerIdOrderByStartDesc(userId);
            case FUTURE:
                return bookingRepository.findFutureByBooker(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findPastByBooker(userId, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findUserBookingsWaitingState(userId);
            case REJECTED:
                return bookingRepository.findUserBookingsRejectedState(userId);
            case CURRENT:
                return bookingRepository.findUserBookingsCurrentState(userId, LocalDateTime.now());
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }

    @Override
    public List<Booking> getOwnerBookingsByState(State state, Long userId) {
        valdateUser(userId);
        List<Long> owners = itemRepository.findAll().stream().map(item -> item.getOwner().getId()).collect(Collectors.toList());
        if (!owners.contains(userId)) {
            throw new NotFoundException(userId + "не является владельцем");
        }

        switch (state) {
            case ALL:
                return bookingRepository.findAllByOwnerIdOrderByStartDesc(userId);
            case FUTURE:
                return bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now());
            case PAST:
                return bookingRepository.findPastByOwnerId(userId, LocalDateTime.now());
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllBookingsByOwnerIdAndStateIgnoreCase(Status.valueOf(state.name()), userId);
            case CURRENT:
                return bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now());
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }

    void valdateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId));
    }
}
