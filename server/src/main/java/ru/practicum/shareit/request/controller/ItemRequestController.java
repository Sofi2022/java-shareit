package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Validated
public class ItemRequestController {

//    private final ItemRequestMapper mapper;
//
//    private final ItemRequestImpl service;
//
//    @PostMapping
//    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
//                                     @Valid @RequestBody ItemCreateRequest request) {
//        log.info("Вызван метод addRequest");
//        ItemRequest request1 = mapper.toItemRequest(request, userId);
//        ItemRequest request2 = service.addRequest(userId, request1);
//        return mapper.toDto(request2);
//    }
//
//    @GetMapping
//    public List<ShortItemRequest> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
//        log.info("Вызван метод getRequests");
//        return service.getItemRequests(userId);
//    }
//
//    @GetMapping("/all")
//    public List<ShortItemRequest> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
//                                                 @PositiveOrZero @RequestParam (name = "from", defaultValue = "0") Integer from,
//                                                 @RequestParam(name = "size", required = false) @Min(1) Integer size) {
//        log.info("Вызван метод get all");
//        if (size != null) {
//            log.info("Вызван метод get all with paginating");
//            int page = from / size;
//            final PageRequest pageRequest = PageRequest.of(page, size);
//            List<ItemRequest> result = service.getAllWithPage(pageRequest, userId);
//            return mapper.toShortList(result);
//        }
//        return service.getAllRequests(userId);
//    }
//
//    @GetMapping("/{requestId}")
//    public ShortItemRequest getRequestById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long requestId) {
//        log.info("Вызван метод getRequestById");
//        return service.getById(userId, requestId);
//    }
}
