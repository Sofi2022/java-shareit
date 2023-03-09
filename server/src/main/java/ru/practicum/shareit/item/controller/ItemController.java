package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentMapper commentMapper;

    private final ItemMapper itemMapper;

    @PostMapping
    public ItemResponse addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestBody ItemCreate itemCreate) {
        return itemMapper.toItemDto(itemService.addItem(itemMapper.toItem(itemCreate, userId), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId, @RequestBody UpdateItemDto updateItemDto) {
        return itemMapper.toItemDto(itemService.updateItem(itemMapper.toItem(updateItemDto, userId), itemId, userId));
    }

    @GetMapping("/{itemId}")
    public ItemResponseWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping()
    public List<ItemResponseWithBooking> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchItem(@RequestParam String text) {
        return itemService.searchByName(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId,
                                  @RequestBody CommentCreateDto comment) {
        return commentMapper.toCommentDto(itemService.addComment(userId, itemId, commentMapper.toComment(comment, userId, itemId)));
    }
}
