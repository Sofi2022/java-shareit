package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookingMapper.class, CommentMapper.class, ItemMapper.class, UserMapper.class})
public interface ItemRequestMapper {

    //@Mapping(target = "requester", source = "userId")
    ItemRequest toItemRequest(ItemCreateRequest request, Long userId);

    ItemRequestDto toDto(ItemRequest request);

    List<ItemRequestDto> toListDto(List<ItemRequest> result);

    //ItemRequestDtoWithItem toRequestWithItems(ItemRequest request, List<Item> items);

    //List<ItemRequestDtoWithItem> toItemListDto(ItemRequest request, List<ItemDto> items);

    //ItemRequestDtoWithItem toItemRequestDtoWithItem(ItemRequest itemRequest, List<ItemDto> items);
}
