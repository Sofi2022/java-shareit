package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreate;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemCreate itemCreate) {
        log.info("Gateway : вызван метод addItem");
        return itemClient.createItem(userId, itemCreate);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId, @RequestBody UpdateItemDto updateItemDto) {
        log.info("Gateway : вызван метод updateItem");
        return itemClient.updateItem(userId, itemId, updateItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Gateway : вызван метод getItemById");
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway : вызван метод getUserItems");
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        log.info("Gateway : вызван метод searchItem");
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long itemId,
                                      @Valid @RequestBody CommentCreateDto comment) {
        log.info("Gateway : вызван метод addComment");
        return itemClient.addComment(userId, itemId, comment);
    }
}

