package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.item.dto.ItemDtoWithAvailable;

import javax.validation.Valid;
import java.util.List;

@Valid
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Jacksonized
public class ItemRequestDtoWithItem {

    private ItemRequestDto itemRequest;

    private List<ItemDtoWithAvailable> items;
}
