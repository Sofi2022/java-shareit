package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoWithAvailable;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class UpdateBookingDto {

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDtoWithAvailable item;

    private UserDto booker;

    private Status status;
}
