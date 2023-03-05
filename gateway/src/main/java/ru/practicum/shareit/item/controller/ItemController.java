//package ru.practicum.shareit.item.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
//import java.util.List;
//import java.util.stream.Collectors;


//@RestController
//@RequestMapping("/items")
//@RequiredArgsConstructor
//public class ItemController {

//    private final ItemClient itemClient;
//
//    @PostMapping
//    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
//                                          @Valid @RequestBody ItemCreate itemCreate) {
//        return itemClient.createItem(userId, itemCreate);
    }
//
////    @PatchMapping("/{itemId}")
////    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
////                                   @PathVariable long itemId, @RequestBody UpdateItemDto updateItemDto) {
////        return itemClient.updateItem(userId, itemId, updateItemDto);
////    }
//
//    @GetMapping("/{itemId}")
//    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
//        return itemClient.getItemById(userId, itemId);
//    }
//
//    @GetMapping()
//    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
//        return itemClient.getUserItems(userId);
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
//}