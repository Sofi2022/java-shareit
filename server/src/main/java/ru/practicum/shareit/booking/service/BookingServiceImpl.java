package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;


    private void validate(Booking booking, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало бронирования не может быть в прошлом времени");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Завершение бронирования не может быть в прошлом времени");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Завершение бронирования не может быть раньше его начала");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Эта вещь недоступна для бронирования " + item.getId());
        }
    }

    private void validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId));
    }

    @Override
    @Transactional
    public Booking addBooking(Booking booking, Long itemId, Long userId) {
        validate(booking, itemId);

        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
        ;
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Нельзя забронировать свою вещь " + itemId);
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }


    @Override
    @Transactional
    public Booking update(Boolean isApprove, Long bookingId, Long userId, Booking booking) {
        if (isApprove == null) {
            Booking bookingForUpdate = bookingRepository.findById(bookingId).orElseThrow(()
                    -> new NotFoundException("Такого букинга нет " + bookingId));
            if (bookingForUpdate.getItem().getOwner().getId() == userId) {
                if (booking.getStart() != null) {
                    bookingForUpdate.setStart(booking.getStart());
                }
                if (booking.getEnd() != null) {
                    bookingForUpdate.setEnd(booking.getEnd());
                }
                if (booking.getStatus() != null) {
                    bookingForUpdate.setStatus(booking.getStatus());
                }
                if (booking.getItem() != null) {
                    bookingForUpdate.setItem(booking.getItem());
                }
                if (booking.getBooker() != null) {
                    bookingForUpdate.setBooker(booking.getBooker());
                }
                return bookingRepository.save(bookingForUpdate);
            } else {
                throw new ValidationException("Вы не являетесь владельцем данной вещи");
            }
        } else {
            return approveOrRejectBooking(bookingId, isApprove, userId);
        }
    }


    @Override
    @Transactional
    public Booking approveOrRejectBooking(Long bookingId, Boolean isApprove, Long userid) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Такого букинга нет " + bookingId));
        if (booking.getItem().getOwner().getId() == userid) {
            if (isApprove) {
                if (booking.getStatus() == Status.APPROVED) {
                    throw new ValidationException("Статус уже подтвержден " + booking.getStatus());
                }
                booking.setStatus(Status.APPROVED);
                return bookingRepository.save(booking);
            } else {
                booking.setStatus(Status.REJECTED);
                return bookingRepository.save(booking);
            }
        } else {
            throw new NotFoundException("Вы не являетесь владельцем данной вещи");
        }
    }


    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Такого букинга нет "
                + bookingId));
        validateUser(userId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return booking;
        } else {
            throw new NotFoundException(userId + " не является владельцем вещи/автором бронирования, " +
                    "к которому относится бронирование");
        }
    }

    @Override
    public List<Booking> getAllUserBookingsById(Long userId) {
        validateUser(userId);
        List<Booking> result = bookingRepository.findBookingByBookerIdOrderByStartDesc(userId);
        return result;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getAllUserBookings(Integer size, Integer from, Long userId, State state) {
        validateUser(userId);
        if (size != 0 && from != 0) {
            int page = from / size;
            final PageRequest pageRequest = PageRequest.of(page, size);
            return getAllWithPage(pageRequest, userId);
        } else {
            return getBookingsByState(state, userId);
        }
    }


    @Override
    public List<Booking> getAllWithPage(PageRequest pageRequest, long userId) {
        Page<Booking> result = bookingRepository.findBookingsByBookerIdOrderByStartDesc(pageRequest, userId);
        return result.getContent();
    }


    @Override
    public List<Booking> getBookingsByState(State state, Long userId) {
        switch (state) {
            case ALL:
                return bookingRepository.findBookingByBookerIdOrderByStartDesc(userId);
            case REJECTED:
                bookingRepository.findUserBookingsRejectedState(userId);
            case CURRENT:
                return bookingRepository.findUserBookingsCurrentState(userId, LocalDateTime.now().withNano(0));
            case FUTURE:
                return bookingRepository.findFutureByBooker(userId, LocalDateTime.now().withNano(0));
            case PAST:
                return bookingRepository.findPastByBooker(userId, LocalDateTime.now().withNano(0));
            case WAITING:
                return bookingRepository.findUserBookingsWaitingState(userId);
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }


    @Override
    public List<Booking> getOwnerBookings(Integer size, Integer from, Long userId, State state) {
        validateUser(userId);
        List<Long> owners = itemRepository.findAll().stream().map(item -> item.getOwner().getId()).collect(Collectors.toList());
        if (!owners.contains(userId)) {
            throw new NotFoundException(userId + " не является владельцем");
        }
        if (from != 0 && size != 0) {
            int page = from / size;
            final PageRequest pageRequest = PageRequest.of(page, size);
            return getAllByOwnerWithPage(pageRequest, userId);
        } else {
            return getOwnerBookingsByState(state, userId);
        }
    }


    @Override
    public List<Booking> getAllByOwnerWithPage(PageRequest pageRequest, Long userId) {
        Page<Booking> result = bookingRepository.findAllByItem_OwnerIdOrderByStartDesc(pageRequest, userId);
        return result.getContent();
    }

    @Override
    public List<Booking> getOwnerBookingsByState(State state, Long userId) {
        switch (state) {
            case ALL:
                return bookingRepository.findAllByOwnerIdOrderByStartDesc(userId);
            case FUTURE:
                return bookingRepository.findFutureByOwnerId(userId, LocalDateTime.now().withNano(0));
            case PAST:
                return bookingRepository.findPastByOwnerId(userId, LocalDateTime.now().withNano(0));
            case WAITING:
            case REJECTED:
                return bookingRepository.findAllBookingsByOwnerIdAndStateIgnoreCase(Status.valueOf(state.name()), userId);
            case CURRENT:
                return bookingRepository.findCurrentByOwnerId(userId, LocalDateTime.now().withNano(0));
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }
}
