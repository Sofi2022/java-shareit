package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequest addRequest(Long userId, ItemRequest request);

    List<ItemRequestDto> getItemRequests(Long userId);

    List<ItemRequest> getAllRequests();

    List<ItemRequest> getAllWithPage(PageRequest pageRequest, long userId);

    ItemRequest getById(long userId);
}
