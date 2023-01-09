package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestMapper mapper;

    private final ItemRequestImpl service;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,  @Valid @RequestBody ItemCreateRequest request){
        System.out.println("create: " + request);
        ItemRequest request1 = mapper.toItemRequest(request, userId);
        System.out.println("1: " + request1);
        ItemRequest request2 = service.addRequest(userId, request1);
        System.out.println("2: " + request2);
        return mapper.toDto(request2);
        //return mapper.toDto(service.addRequest(userId, mapper.toItemRequest(request, userId)));
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId){
        //List<ItemRequest> result = service.getRequests(userId);
        //return mapper.toListDto(result);
        return service.getItemRequests(userId);
    }

//    @GetMapping("/all")
//    public List<ItemRequestDtoWithItem> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
//                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0",
//                                                    required = false) @Min(0) Integer from,
//                                                    @RequestParam(name = "size", required = false) @Min(1) Integer size){
//        log.info("Вызван метод get all");
//        if(size != null){
//            log.info("Вызван метод get all with paginating");
//            int page = from / size;
//            final PageRequest pageRequest = PageRequest.of(page, size);
//            List<ItemRequest> result = service.getAllWithPage(pageRequest, userId);
//            return mapper.toItemListDto(result);
//        }
//        List<ItemRequest> result = service.getAllRequests();
//        return mapper.toItemListDto(result);
//    }

    @GetMapping("/requestId")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId){
        ItemRequest request = service.getById(userId);
        return mapper.toDto(request);
    }

}
