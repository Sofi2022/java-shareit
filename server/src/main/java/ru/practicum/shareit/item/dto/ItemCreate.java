package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreate {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private UserDto owner;

    private Long requestId;
}
