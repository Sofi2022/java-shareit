package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoWithAvailable;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingResponse {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Status status;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserDto booker;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ItemDtoWithAvailable item;
}
