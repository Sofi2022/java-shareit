package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Spy
    private ItemMapper itemMapper;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private Item item1;

    private Item item2;

    private User owner1;

    private final Long ownerId1 = 1L;

    private final Long ownerId2 = 2L;

    private final Long itemId1 = 1L;

    private final Long itemId2 = 2L;

    private ItemRequest request;

    private User requester;

    private User owner2;

    Set<Item> itemsRequest;

    @BeforeEach
    void init() {
        owner1 = new User(1, "Olga", "Olga123@mail.ru");
        userRepository.save(owner1);
        item1 = new Item(1, "Flowers", "Roses", true, owner1,
                new HashSet<>(), null);

        owner2 = new User(2, "Petya", "Petya1@mail.ru");
        userRepository.save(owner2);
        item2 = new Item(3, "Карандаш", "Цветной", true, owner2,
                new HashSet<>(), null);
        requester = new User(5L, "Requester", "Requester123@mail.ru");

        itemsRequest = new HashSet<>();
        itemsRequest.add(item2);

        request = new ItemRequest(1, "Request", requester, LocalDateTime.now().withNano(0),
                itemsRequest);
        itemMapper = Mappers.getMapper(ItemMapper.class);
    }

    @Test
    void addItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.save(item1)).thenReturn(item1);

        Item actualItem = itemService.addItem(item1, ownerId1);
        assertEquals(item1, actualItem);
        verify(itemRepository, times(1)).save(item1);
    }


    @Test
    void addItem_WithRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner2));
        when(itemRepository.save(any(Item.class))).thenReturn(item2);

        Item actualItem = itemService.addItem(item2, ownerId2);
        assertEquals(item2, actualItem);
        verify(itemRepository, times(1)).save(item2);
    }


    @Test
    void addItem_WithRequestNull() {
        ItemRequest nullRequest = new ItemRequest(0, " ", requester, LocalDateTime.now().withNano(0),
                new HashSet<>());
        item2.setRequest(nullRequest);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner2));
        when(itemRepository.save(any(Item.class))).thenReturn(item2);

        Item actualItem = itemService.addItem(item2, ownerId2);
        assertEquals(item2, actualItem);
        verify(itemRepository, times(1)).save(item2);
    }

    @Test
    void updateItem_Ok() {
        Item update = new Item();
        update.setId(1L);
        update.setName("FlowersUpdated");
        update.setDescription("RosesUpdated");
        update.setOwner(owner2);
        update.setAvailable(false);

        Item updatedItem = new Item(1L, "FlowersUpdated", "RosesUpdated", false, owner2,
                new HashSet<>(), null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        itemService.updateItem(update, itemId1, ownerId1);
        verify(itemRepository, times(1)).save(item1);

        Item actual = itemService.getById(itemId1);
        assertEquals(updatedItem, actual);
    }


    @Test
    void updateItem_Ok_OnlyAvailable() { // item1 = new Item(1, "Flowers", "Roses", true, owner1,
        //new HashSet<>(), null);
        Item update = new Item();
        update.setAvailable(false);

        Item updatedItem = new Item(1L, "Flowers", "Roses", false, owner1,
                new HashSet<>(), null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        itemService.updateItem(update, itemId1, ownerId1);
        verify(itemRepository, times(1)).save(item1);

        Item actual = itemService.getById(itemId1);
        assertEquals(updatedItem, actual);
    }


    @Test
    void updateItem_Ok_OnlyName() {
        Item update = new Item();
        update.setName("UpdatedName");

        Item updatedItem = new Item(1L, "UpdatedName", "Roses", true, owner1,
                new HashSet<>(), null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        itemService.updateItem(update, itemId1, ownerId1);
        verify(itemRepository, times(1)).save(item1);

        Item actual = itemService.getById(itemId1);
        assertEquals(updatedItem, actual);
    }


    @Test
    void updateItem_WithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(item1, itemId1, 5L),
                "Вы не имеете права обновить объект");
    }

    @Test
    void getItemById_WithoutBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        itemService.getItemById(ownerId2, itemId1);
        verify(bookingRepository, times(0)).findLastBookingsByItemIdOrderByStartDesc(itemId1,
                LocalDateTime.now().withNano(0));
    }


    @Test
    void getItemById_WithBookings() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        List<Booking> lastBookings = new ArrayList<>();
        LocalDateTime start = LocalDateTime.of(2023, 6, 26, 12, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 26, 18, 0);
        lastBookings.add(new Booking(1L, start, end, item1, owner2, Status.WAITING));

        List<Booking> nextBookings = new ArrayList<>();
        nextBookings.add(new Booking(2L, start.plusMonths(1), end.plusMonths(1), item1, owner2, Status.WAITING));

        when(bookingRepository.findLastBookingsByItemIdOrderByStartDesc(anyLong(),
                any(LocalDateTime.class))).thenReturn(lastBookings);
        when(bookingRepository.findNextBookingsByItemIdOrderByStartAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(nextBookings);

        ShortBookingDto lastBookingShort = new ShortBookingDto();
        lastBookingShort.setId(1L);
        lastBookingShort.setBookerId(ownerId2);

        ShortBookingDto nextBookingShort = new ShortBookingDto();
        nextBookingShort.setId(2L);
        nextBookingShort.setBookerId(ownerId2);

//        ItemResponseWithBooking expected = new ItemResponseWithBooking();
//        expected.setId(1L);
//        expected.setName("Flowers");
//        expected.setAvailable(true);
//        expected.setDescription("Roses");
//        expected.setLastBooking(lastBookingShort);
//        expected.setNextBooking(nextBookingShort);

        //ItemResponseWithBooking actual =
                itemService.getItemById(itemId1, ownerId1);
       // assertEquals(expected, actual);

        verify(bookingRepository, times(1)).findLastBookingsByItemIdOrderByStartDesc(itemId1,
                LocalDateTime.now().withNano(0));
        verify(bookingRepository, times(1)).findNextBookingsByItemIdOrderByStartAsc(itemId1,
                LocalDateTime.now().withNano(0));
    }

    @Test
    void getUserItems() {
        Item item3 = new Item(3L, "Лего", "Детский", true, owner1, null, null);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item3);

        when(itemRepository.getUserItems(anyLong())).thenReturn(items);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        List<ItemResponseWithBooking> actual = itemService.getUserItems(ownerId1);
        assertEquals(2, actual.size());
    }


    @Test
    void searchByEmptyName() {

        List<Item> actual = itemService.searchByName("");
        assertEquals(0, actual.size());
    }


    @Test
    void searchByName() {
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase(anyString(), anyString())).thenReturn(List.of(item1));

        List<Item> items = new ArrayList<>();
        items.add(item1);

        List<Item> actual = itemService.searchByName("Flowers");
        assertEquals(1, actual.size());
    }

    @Test
    void searchByDescription() {
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase(anyString(), anyString())).thenReturn(List.of(item1));

        List<Item> items = new ArrayList<>();
        items.add(item1);

        List<Item> actual = itemService.searchByName("Roses");
        assertEquals(1, actual.size());
    }

    @Test
    void addComment() {
        LocalDateTime start = LocalDateTime.of(2023, 6, 26, 12, 30);
        LocalDateTime end = LocalDateTime.of(2023, 6, 26, 18, 0);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1L, start, end, item1, owner2, Status.APPROVED));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item1);
        comment.setAuthor(owner2);
        comment.setText("Все окей");
        comment.setCreated(LocalDateTime.now().withNano(0));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(bookingRepository.findBookingByBookerAndItemId(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(bookings);
        itemService.addComment(ownerId2, itemId1, comment);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addComment_ThrowsException() {
        Long itemId3 = 3L;

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item1);
        comment.setAuthor(owner2);
        comment.setText("Все окей");
        comment.setCreated(LocalDateTime.now().withNano(0));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner1));
        when(bookingRepository.findBookingByBookerAndItemId(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        assertThrows(ValidationException.class, () -> itemService.addComment(3L, itemId1, comment),
                "Вы не бронировали данную вещь " + itemId3);
        verify(commentRepository, times(0)).save(comment);
    }


    @Test
    void findAllByRequestIds() {
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(2);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        when(itemRepository.findAllByRequestIds(anyList())).thenReturn(items);
        List<Item> actual = itemService.findAllByRequestIds(ids);
        assertEquals(2, actual.size());
    }
}