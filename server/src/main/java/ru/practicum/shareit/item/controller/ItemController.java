package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper mapper;

    private final CommentMapper commentMapper;

    @PostMapping
    public ItemResponse addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                               // @Valid
                                @RequestBody ItemCreateRequest itemCreateRequest) {
        return mapper.toItemDto(itemService.addItem(mapper.toItem(itemCreateRequest, userId)));
    }

    @PatchMapping("/{itemId}")
    public ItemResponse updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long itemId, @RequestBody UpdateItemDto updateItemDto) {
        return mapper.toItemDto(itemService.updateItem(mapper.toItem(updateItemDto, userId), itemId, userId));
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
        return itemService.searchByName(text).stream().map(mapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId,
                                  //@Valid
                                  @RequestBody CommentCreateDto comment) {
        return commentMapper.toCommentDto(itemService.addComment(userId, itemId, commentMapper.toComment(comment)));
    }
}
