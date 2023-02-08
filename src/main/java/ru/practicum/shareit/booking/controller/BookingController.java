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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingMapper mapper;

    private final BookingService bookingService;


    @PostMapping
    public BookingResponse addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody BookingCreateRequest booking) {
        return mapper.toBookingDto(bookingService.addBooking(mapper.toBooking(booking), booking.getItemId(), userId));
    }


    @PatchMapping("/{bookingId}")
    public BookingResponse updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                         @RequestBody(required = false) UpdateBookingDto bookingDto, @RequestParam(required = false, name = "approved")
                                         Boolean approved) {
        //return mapper.toBookingDto(bookingService.approveOrRejectBooking(bookingId, approved, userId));
        Booking booking = mapper.toBooking(bookingDto);

        Booking update = bookingService.update(approved, bookingId, userId, booking);
        return mapper.toBookingDto(update);
    }


    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return mapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }


    @GetMapping
    public List<BookingResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                    State state, @PositiveOrZero @RequestParam(name = "from",
            defaultValue = "0", required = false) @Min(0) Integer from, @RequestParam(name = "size", required = false)
                                                    @Min(1) Integer size) {

        return mapper.toListBookingDto(bookingService.getAllUserBookings(size, from, userId, state));
    }


    @GetMapping("/owner")
    public List<BookingResponse> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                  State state, @PositiveOrZero @RequestParam(name = "from",
            defaultValue = "0", required = false) @Min(0) Integer from, @RequestParam(name = "size", required = false)
                                                  @Min(1) Integer size) {

        return mapper.toListBookingDto(bookingService.getOwnerBookings(size, from, userId, state));
    }
}
