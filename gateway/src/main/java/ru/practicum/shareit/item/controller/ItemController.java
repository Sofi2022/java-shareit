package ru.practicum.shareit.item.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
//import java.util.List;
//import java.util.stream.Collectors;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemCreate itemCreate) {
        return itemClient.createItem(userId, itemCreate);
    }

//    @PatchMapping("/{itemId}")
//    public ItemResponse updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
//                                   @PathVariable long itemId, @RequestBody UpdateItemDto updateItemDto) {
//        return mapper.toItemDto(itemService.updateItem(mapper.toItem(updateItemDto, userId), itemId, userId));
//    }
//
//    @GetMapping("/{itemId}")
//    public ItemResponseWithBooking getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
//        return itemService.getItemById(userId, itemId);
//    }
//
//    @GetMapping()
//    public List<ItemResponseWithBooking> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
//        return itemService.getUserItems(userId);
//    }
//
//    @GetMapping("/search")
//    public List<ItemResponse> searchItem(@RequestParam String text) {
//        return itemService.searchByName(text).stream().map(mapper::toItemDto).collect(Collectors.toList());
//    }
//
//    @PostMapping("/{itemId}/comment")
//    CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId,
//                                  @Valid @RequestBody CommentCreateDto comment) {
//        return commentMapper.toCommentDto(itemService.addComment(userId, itemId, commentMapper.toComment(comment, userId, itemId)));
//    }
}