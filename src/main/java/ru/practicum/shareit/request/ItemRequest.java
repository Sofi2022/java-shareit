package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemRequest {

    private long id;

    private String description;

    private User requestor;

    private LocalDate created;
}
