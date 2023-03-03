package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ShortItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestImpl implements RequestService {

    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final ItemService itemService;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;


    @Override
    @Transactional
    public ItemRequest addRequest(Long userId, ItemRequest request) {
        User requester = validateUser(userId);
        request.setRequester(requester);
        return repository.save(request);
    }


    @Override
    public List<ShortItemRequest> getItemRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет "
                + userId));
        List<ItemRequest> itemRequests =
                repository.findAllByRequestor(user);
        if (itemRequests.size() == 0) {
            List<ItemRequest> result = new ArrayList<>();
            return mapper.toShortList(result);
        }
        Map<Integer, List<Item>> map =
                itemService.findAllByRequestIds(itemRequests.stream()
                                .map(ItemRequest::getId)
                                .collect(Collectors.toList()))
                        .stream()
                        .collect(Collectors.groupingBy(it -> it.getRequest().getId(), Collectors.toList()));
        return itemRequests.stream()
                .map(mapper::toShortRequest)
                .peek(itemRequest -> itemRequest
                        .setItems(itemMapper.toListItemResponse(new ArrayList<>(map.getOrDefault(itemRequest.getId(),
                                Collections.emptyList())))))
                .collect(Collectors.toList());
    }


    @Override
    public List<ShortItemRequest> getAllRequests(Long userId) {
        List<ItemRequest> result = repository.getItemRequestByUserId(userId);
        return mapper.toShortList(result);
    }

    @Override
    public List<ItemRequest> getAllWithPage(PageRequest pageRequest, long userId) {
        Page<ItemRequest> result = repository.findItemRequestByRequester_IdIsNot(pageRequest, userId);
        return result.getContent();
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId));
    }

    @Override
    public ShortItemRequest getById(long userId, long requestId) {
        validateUser(userId);
        return mapper.toShortRequest(repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Такого запроса нет " + requestId)));
    }
}
