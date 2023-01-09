package ru.practicum.shareit.item.dto;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@Mapper(componentModel = "spring", uses = {CommentMapper.class, BookingMapper.class})
public interface ItemMapper {

    ItemResponse toItemDto(Item item);

    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemCreateRequest itemCreateRequest, Long userId);

    @Mapping(target = "owner.id", source = "userId")
    @Mapping(target = "id", ignore = true)
    Item toItem(UpdateItemDto updateItemDto, Long userId);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "comments", source = "item.comments")
    ItemResponseWithBooking toItemWithBooking(Item item, ShortBookingDto lastBooking, ShortBookingDto nextBooking);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto toShortItemDto(Item item);

    @Mapping(target = "ownerId", source = "owner.id")
    List<ItemDto> toListDto(List<Item> items);
}
