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

    private final ItemRequestMapper itemRequestMapper;


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
            return itemRequestMapper.toShortList(result);
        }
        Map<Integer, List<Item>> map =
                itemService.findAllByRequestIds(itemRequests.stream()
                                .map(ItemRequest::getId)
                                .collect(Collectors.toList()))
                        .stream()
                        .collect(Collectors.groupingBy(it -> it.getRequest().getId(), Collectors.toList()));
        return itemRequests.stream()
                .map(itemRequestMapper::toShortRequest)
                .peek(itemRequest -> itemRequest
                        .setItems(itemMapper.toListItemResponse(new ArrayList<>(map.getOrDefault(itemRequest.getId(),
                                Collections.emptyList())))))
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortItemRequest> getAllWithPage(Long userId, Integer from, Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        Page<ItemRequest> result = repository.findItemRequestByRequester_IdIsNot(pageRequest, userId);
        List<ItemRequest> getFromContent = result.getContent();
        List<ShortItemRequest> shortItemRequests = mapper.toShortList(getFromContent);
        return shortItemRequests;
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId));
    }

    @Override
    public ShortItemRequest getById(long userId, long requestId) {
        validateUser(userId);
        return itemRequestMapper.toShortRequest(repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Такого запроса нет " + requestId)));
    }
}
