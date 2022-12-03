package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(Item item);

    Item updateItem(Item item, Long itemId, Long userId);

    Item getItemById(Long itemId);

    List<Item> getUserItems(Long userId);

    List<Item> searchByName(String text);
}
