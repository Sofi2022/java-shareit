package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingService bookingService;

    private final BookingMapper bookingMapper;


    @PostMapping
    public BookingResponse addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody BookingCreateRequest booking) {
        return bookingMapper.toBookingDto(bookingService.addBooking(bookingMapper.toBooking(booking),
                booking.getItemId(), userId));
    }


    @PatchMapping("/{bookingId}")
    public BookingResponse updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                         @RequestBody(required = false) UpdateBookingDto bookingDto, @RequestParam(required = false, name = "approved")
                                         Boolean approved) {
        Booking booking = bookingMapper.toBooking(bookingDto);

        Booking update = bookingService.update(approved, bookingId, userId, booking);
        return bookingMapper.toBookingDto(update);
    }


    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }


    @GetMapping
    public List<BookingResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                    State state, @PositiveOrZero @RequestParam(name = "from",
            defaultValue = "0", required = false) Integer from, @RequestParam(name = "size", defaultValue = "10",
            required = false) Integer size) {
        log.info("Server: вызван метод getAllUserBookings");
        log.info("{}, {}, {}, {}", userId, state, from, size);
        List<Booking> bookings = bookingService.getAllUserBookings(size, from, userId, state);
        List<BookingResponse> result = bookingMapper.toListBookingDto(bookings);
        return result;
    }


    @GetMapping("/owner")
    public List<BookingResponse> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                  State state, @RequestParam(name = "from",
            defaultValue = "0", required = false) Integer from, @RequestParam(name = "size", defaultValue = "10",
            required = false) Integer size) {

        return bookingMapper.toListBookingDto(bookingService.getOwnerBookings(size, from, userId, state));
    }
}


