package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
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

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {

    private final BookingService bookingService;


    @PostMapping
    public BookingResponse addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody BookingCreateRequest booking) {
        return Mappers.getMapper(BookingMapper.class).toBookingDto(bookingService.addBooking(Mappers.getMapper(BookingMapper.class).toBooking(booking), booking.getItemId(), userId));
    }


    @PatchMapping("/{bookingId}")
    public BookingResponse updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
                                         @RequestBody(required = false) UpdateBookingDto bookingDto, @RequestParam(required = false, name = "approved")
                                         Boolean approved) {
        //return mapper.toBookingDto(bookingService.approveOrRejectBooking(bookingId, approved, userId));
        Booking booking = Mappers.getMapper(BookingMapper.class).toBooking(bookingDto);

        Booking update = bookingService.update(approved, bookingId, userId, booking);
        return Mappers.getMapper(BookingMapper.class).toBookingDto(update);
    }


    @GetMapping("/{bookingId}")
    public BookingResponse getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return Mappers.getMapper(BookingMapper.class).toBookingDto(bookingService.getBookingById(bookingId, userId));
    }


    @GetMapping
    public List<BookingResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                    State state, @RequestParam(name = "from",
            defaultValue = "0", required = false) Integer from, @RequestParam(name = "size", required = false) Integer size) {

        return Mappers.getMapper(BookingMapper.class).toListBookingDto(bookingService.getAllUserBookings(size, from, userId, state));
    }


    @GetMapping("/owner")
    public List<BookingResponse> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(required = false, name = "state", defaultValue = "ALL")
                                                  State state, @RequestParam(name = "from",
            defaultValue = "0", required = false) Integer from, @RequestParam(name = "size", required = false) Integer size) {

        return Mappers.getMapper(BookingMapper.class).toListBookingDto(bookingService.getOwnerBookings(size, from, userId, state));
    }
}


