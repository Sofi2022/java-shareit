package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class ItemResponseWithBooking {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ShortBookingDto lastBooking;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ShortBookingDto nextBooking;

    private Set<CommentResponseDto> comments;
}
