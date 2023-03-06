package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.State;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;


@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody BookingCreateRequest booking) {
        return bookingClient.createBooking(userId, booking);
    }


    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                                @RequestBody(required = false) UpdateBookingDto bookingDto,
                                                @RequestParam(required = false, name = "approved")
                                                Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, bookingDto, approved);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }


    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                     State state,
                                                     @RequestParam(name = "from", defaultValue = "0",
                                                             required = false) Integer from, @Min(1) @RequestParam(name = "size", defaultValue = "10", required = false)
                                                     Integer size) {

        try {
            State status = state;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        log.info("Gateway: вызван метод getAllUserBookings");
        log.info("{}, {}, {}, {}", userId, state, from, size);
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                   State state, @PositiveOrZero @RequestParam(name = "from",
            defaultValue = "0", required = false) Integer from, @Min(1) @RequestParam(name = "size", defaultValue = "10", required = false)
                                                   Integer size) {

        try {
            State status = state;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        log.info("Gateway: вызван метод getOwnerBookings");
        log.info("{}, {}, {}, {}", userId, state, from, size);
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}


