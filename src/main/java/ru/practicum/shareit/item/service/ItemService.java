package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemResponseWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Item item);

    Item updateItem(Item item, Long itemId, Long userId);

    ItemResponseWithBooking getItemById(Long userId, Long itemId);

    List<ItemResponseWithBooking> getUserItems(Long userId);

    List<Item> searchByName(String text);

    Comment addComment(Long userId, Long itemId, Comment comment);

    List<Item> findAllByRequestIds(List<Integer> ids);
}
