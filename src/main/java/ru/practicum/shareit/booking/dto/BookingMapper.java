package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface BookingMapper {

    Booking toBooking(BookingCreateRequest bookingCreateRequest);

    BookingResponse toBookingDto(Booking booking);

    Booking toBooking(UpdateBookingDto updateBookingDto);

    @Mapping(target = "bookerId", source = "booker.id")
    ShortBookingDto toShortBooking(Booking booking);

    List<BookingResponse> toListBookingDto(List<Booking> result);
}
