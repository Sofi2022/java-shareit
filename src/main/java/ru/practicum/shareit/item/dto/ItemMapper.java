package ru.practicum.shareit.item.dto;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ItemMapper {

    ItemResponse toItemDto(Item item);

    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemCreateRequest itemCreateRequest, Long userId);

    @Mapping(target = "owner.id", source = "userId")
    Item toItem(ItemResponse itemResponse, Long userId);

    @Mapping(target = "owner.id", source = "userId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toItem(UpdateItemDto updateItemDto, Long userId);
}
