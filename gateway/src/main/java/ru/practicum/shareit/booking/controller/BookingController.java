package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;
//import ru.practicum.shareit.booking.model.State;
//import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingController {


    //private final BookingService bookingService;

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody BookingCreateRequest booking) {
        return bookingClient.createBooking(userId, booking);
    }


//    @PatchMapping("/{bookingId}")
//    public BookingResponse updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId,
//                                         UpdateBookingDto bookingDto, @RequestParam(required = false, name = "approved") Boolean approved) {
//        if (approved != null) {
//            return mapper.toBookingDto(bookingService.approveOrRejectBooking(bookingId, approved, userId));
//        }
//        return mapper.toBookingDto(bookingService.update(bookingId, userId, mapper.toBooking(bookingDto)));
//    }
//
//
//    @GetMapping("/{bookingId}")
//    public BookingResponse getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
//        return mapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
//    }
//
//
//    @GetMapping
//    public List<BookingResponse> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                                    @RequestParam(required = false, name = "state", defaultValue = "ALL")
//                                                    State state) {
//        return mapper.toListBookingDto(bookingService.getBookingsByState(state, userId));
//    }
//
//
//    @GetMapping("/owner")
//    public List<BookingResponse> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
//                                                 @RequestParam(required = false, name = "state", defaultValue = "ALL")
//                                                 State state) {
//        return bookingService.getOwnerBookingsByState(state, userId).stream().map(mapper::toBookingDto).collect(Collectors.toList());
//    }
}
