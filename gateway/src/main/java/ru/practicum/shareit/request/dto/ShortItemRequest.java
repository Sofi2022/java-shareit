package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ShortItemRequest {

    private int id;

    private String description;

    private LocalDateTime created;

    private List<ItemResponse> items;
}
