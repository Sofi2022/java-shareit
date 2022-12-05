package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private long itemId = 0;

    Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(++itemId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return getItemById(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Long> getItemsIds() {
        return List.copyOf(items.keySet());
    }

    @Override
    public List<Item> getItems() {
        return List.copyOf(items.values());
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        List<Item> items = getItems();
        return items.stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByName(String text) {
        String textToLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(textToLowerCase)
                        || item.getDescription().toLowerCase().contains(textToLowerCase))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
