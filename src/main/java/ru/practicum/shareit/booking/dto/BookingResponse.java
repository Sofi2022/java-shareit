package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

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
    private User booker;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Item item;
}