package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper mapper;

    @PostMapping
    public ItemResponse addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                @Valid @RequestBody ItemCreateRequest itemCreateRequest) {
        return mapper.toItemDto(itemService.addItem(mapper.toItem(itemCreateRequest, userId)));
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId, @RequestBody UpdateItemDto updateItemDto) {
        return mapper.toItemDto(itemService.updateItem(mapper.toItem(updateItemDto, userId), itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@PathVariable long itemId) {
        return mapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping()
    public List<ItemResponse> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getUserItems(userId).stream().map(mapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemResponse> searchItem(@RequestParam String text) {
        return itemService.searchByName(text).stream().map(mapper::toItemDto).collect(Collectors.toList());
    }
}
