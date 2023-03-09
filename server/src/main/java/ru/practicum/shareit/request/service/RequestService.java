package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ShortItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequest addRequest(Long userId, ItemRequest request);

    List<ShortItemRequest> getItemRequests(Long userId);

    List<ShortItemRequest> getAllWithPage(Long userId, Integer from, Integer size);

    ShortItemRequest getById(long userId, long requestId);
}
