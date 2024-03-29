package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ShortItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequest addRequest(Long userId, ItemRequest request);

    List<ShortItemRequest> getItemRequests(Long userId);

    List<ShortItemRequest> getAllRequests(Long userId);

    List<ItemRequest> getAllWithPage(PageRequest pageRequest, long userId);

    ShortItemRequest getById(long userId, long requestId);
}
