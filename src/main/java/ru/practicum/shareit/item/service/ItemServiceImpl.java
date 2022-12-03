package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item addItem(Item item) {
        List<Long> usersIds = userService.getUsersIds();
        if (usersIds.contains(item.getOwner().getId())) {
            return itemRepository.addItem(item);
        } else {
            throw new NotFoundException(String.format("Пользователя с таким id нет ", item.getOwner().getId()));
        }
    }

    @Override
    public Item updateItem(Item item, Long itemId, Long userId) {
        Item newItem = itemRepository.getItemById(itemId);
        if (newItem.getOwner().getId() == userId) {
            if (item.getName() != null) {
                newItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                newItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                newItem.setAvailable(item.getAvailable());
            }
            if (item.getOwner() != null) {
                newItem.setOwner(item.getOwner());
            }
            if (item.getRequest() != null) {
                newItem.setRequest(item.getRequest());
            }
            itemRepository.updateItem(newItem);
            return newItem;
        } else {
            throw new NotFoundException("Вы не имеете права обновить объект");
        }
    }

    @Override
    public Item getItemById(Long itemId) {
        List<Long> itemsIds = itemRepository.getItemsIds();
        if (itemsIds.contains(itemId)) {
            return itemRepository.getItemById(itemId);
        } else {
            throw new NotFoundException("Такого пользователя нет");
        }
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return itemRepository.getUserItems(userId);
    }

    @Override
    public List<Item> searchByName(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.searchByName(text);
        return items.stream().filter(Item::getAvailable).collect(Collectors.toList());
    }
}
