package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;
}
