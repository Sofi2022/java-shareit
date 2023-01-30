package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingResponse {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Status status;

    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    //private User booker;

    private UserDto booker;

//    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//    private Item item;

    private ItemDto item;
}
