package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemMapper mapper;

    private final BookingMapper bookingMapper;


    @Override
    @Transactional
    public Item addItem(Item item) {
        List<Long> usersIds = userService.getUsersIds();
        if (usersIds.contains(item.getOwner().getId())) {
            return itemRepository.save(item);
        } else {
            throw new NotFoundException("Пользователя с таким id нет " + item.getOwner().getId());
        }
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Long itemId, Long userId) {
        Item newItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
        if (newItem.getOwner().getId() == userId) {
            if (item.getName() != null) {
                newItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                newItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                newItem.setAvailable(item.getAvailable());
            }
            if (item.getOwner() != null) {
                newItem.setOwner(item.getOwner());
            }
            return itemRepository.save(newItem);
        } else {
            throw new NotFoundException("Вы не имеете права обновить объект");
        }
    }

    @Override
    public ItemResponseWithBooking getItemById(Long userId, Long itemId) {
        List<Long> itemsIds = itemRepository.getItemsIds();
        if (!(itemsIds.contains(itemId))) {
            throw new NotFoundException("Такой вещи нет " + itemId);
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
        if (!(item.getOwner().getId() == userId)) {
            return mapper.toItemWithBooking(item, null, null);
        }
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemIdOrderByStartDesc(itemId, LocalDateTime.now());
        ShortBookingDto lastBookingShort;
        if (lastBookings.isEmpty()) {
            lastBookingShort = null;
        } else {
            lastBookingShort = bookingMapper.toShortBooking(lastBookings.get(0));
        }

        List<Booking> nextBookings = bookingRepository.findNextBookingsByItemIdOrderByStartAsc(itemId, LocalDateTime.now());
        ShortBookingDto nextBookingShort;
        if (nextBookings.isEmpty()) {
            nextBookingShort = null;
        } else {
            nextBookingShort = bookingMapper.toShortBooking(nextBookings.get(0));
        }
        return mapper.toItemWithBooking(item, lastBookingShort, nextBookingShort);
    }

    @Override
    public List<ItemResponseWithBooking> getUserItems(Long userId) {
        List<Item> items = itemRepository.getUserItems(userId);
        return items.stream().map(item -> getItemById(userId, item.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchByName(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> result = itemRepository.findByNameOrDescriptionContainingIgnoreCase(text, text);

        return result.stream().filter(Item::getAvailable).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Comment addComment(Long userId, Long itemId, Comment comment) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет "
                + userId));

        List<Booking> booking = bookingRepository.findBookingByBookerAndItemId(userId, itemId, APPROVED, LocalDateTime.now());
        if (booking.size() != 0) {
            item.getComments().add(comment);
            comment.setItem(item);
            comment.setAuthor(author);
            commentRepository.save(comment);
            return comment;
        } else {
            throw new ValidationException("Вы не бронировали данную вещь " + itemId);
        }
    }

    @Override
    public List<Item> findAllByRequestIds(List<Integer> ids) {
        return itemRepository.findAllByRequestIds(ids);
    }
}
