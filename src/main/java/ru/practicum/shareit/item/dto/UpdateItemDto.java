package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private Long requestId;
}
