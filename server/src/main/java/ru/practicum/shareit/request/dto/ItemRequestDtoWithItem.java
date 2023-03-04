package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ItemRequestDtoWithItem {

    private ItemRequestDto itemRequest;

    private List<ItemDto> items;
}
