package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@AllArgsConstructor
public class UpdateItemDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;
}
