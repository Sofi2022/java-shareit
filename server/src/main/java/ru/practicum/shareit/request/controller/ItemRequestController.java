package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ShortItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestImpl;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ItemRequestController {

    private final ItemRequestMapper mapper;

    private final ItemRequestImpl service;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemCreateRequest request) {
        log.info("Вызван метод addRequest");
        ItemRequest request1 = Mappers.getMapper(ItemRequestMapper.class).toItemRequest(request, userId);
        ItemRequest request2 = service.addRequest(userId, request1);
        return mapper.toDto(request2);
    }

    @GetMapping
    public List<ShortItemRequest> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Вызван метод getRequests");
        return service.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ShortItemRequest> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(name = "from",
            defaultValue = "0") Integer from, @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Вызван метод get all");
        return service.getAllWithPage(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ShortItemRequest getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
        log.info("Вызван метод getRequestById");
        return service.getById(userId, requestId);
    }
}
