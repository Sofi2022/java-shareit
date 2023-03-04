package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemCreateRequest;
import ru.practicum.shareit.request.dto.ShortItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestImplTest {

    @InjectMocks
    ItemRequestImpl requestService;

    @Mock
    private RequestRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemService itemService;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Spy
    private ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private User requester;

    private Long requesterId = 1L;

    private ItemCreateRequest itemCreateRequest;

    private ItemRequest request;

    private ItemRequest request2;

    private Item item1;

    private User owner;

    @BeforeEach
    void init() {
        requester = new User(1L, "Petya", "Petya123@mail.ru");
        itemCreateRequest = new ItemCreateRequest("Description", requester, LocalDateTime.now().withNano(0));
        owner = new User(2L, "Misha", "Misha123@mail.ru");
        item1 = new Item(1L, "Item1", "Descr1", true, owner, null, null);
        Set<Item> items = new HashSet<>();
        items.add(item1);
        request = new ItemRequest(1, "Description", requester, LocalDateTime.now()
                .withNano(0), items);
        request2 = new ItemRequest(2, "Description2", requester, LocalDateTime.now()
                .withNano(0), items);
    }


    @Test
    void addRequest_WithValidUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(repository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequest actual = requestService.addRequest(requesterId, request);

        assertEquals(request, actual);
        verify(repository, times(1)).save(request);
    }


    @Test
    void addRequest_WithNotValidUser() {
        long wrongId = 10L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.addRequest(wrongId, request),
                "Такого пользователя нет " + wrongId);
        verify(repository, never()).save(request);
    }


    @Test
    void getItemRequests() {
        List<ItemRequest> requests = new ArrayList<>();

        List<Item> itemsList = new ArrayList<>();
        itemsList.add(item1);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(repository.findAllByRequestor(any(User.class))).thenReturn(requests);

        List<ShortItemRequest> result = new ArrayList<>();

        List<ShortItemRequest> actual = requestService.getItemRequests(requesterId);

        assertEquals(result, actual);
    }
}