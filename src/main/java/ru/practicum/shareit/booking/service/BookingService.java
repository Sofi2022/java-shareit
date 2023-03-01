package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    Booking addBooking(Booking booking, Long itemId, Long userId);

    Booking approveOrRejectBooking(Long bookingId, Boolean isApprove, Long userId);

    Booking update(Boolean isApprove, Long bookingId, Long userId, Booking booking);

    Booking getBookingById(Long bookingId, Long userid);

    List<Booking> getAllUserBookings(Integer size, Integer from, Long userId, State state);

    List<Booking> getOwnerBookings(Integer size, Integer from, Long userId, State state);

    List<Booking> getAllUserBookingsById(Long userId);

    List<Booking> getAllBookings();

    List<Booking> getBookingsByState(State state, Long userId);

    List<Booking> getOwnerBookingsByState(State state, Long userId);

    List<Booking> getAllByOwnerWithPage(PageRequest pageRequest, Long userId);

    List<Booking> getAllWithPage(PageRequest pageRequest, long userId);
}
