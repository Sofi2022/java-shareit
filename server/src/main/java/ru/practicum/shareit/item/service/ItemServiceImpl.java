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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.Status.APPROVED;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    private final ItemMapper itemMapper;

    private final BookingMapper bookingMapper;


    @Override
    @Transactional
    public Item addItem(Item item, Long userId) {
            if (item.getRequest() == null || item.getRequest().getId() == 0) {
                item.setRequest(null);
            }
            item.setOwner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет " + userId)));
            return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Long itemId, Long userId) {
        validateUser(userId);
        Item actualItem = getById(itemId);
        if (actualItem.getOwner().getId() == userId) {
            if (item.getName() != null) {
                actualItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                actualItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                actualItem.setAvailable(item.getAvailable());
            }
            if (item.getOwner() != null) {
                actualItem.setOwner(item.getOwner());
            }
            return itemRepository.save(actualItem);
        } else {
            throw new NotFoundException("Вы не имеете права обновить объект");
        }
    }

    //        List<Long> itemsIds = itemRepository.getItemsIds();
//        if (!(itemsIds.contains(itemId))) {
//            throw new NotFoundException("Такой вещи нет " + itemId);
//        }
    //getById(itemId);

    @Override
    public ItemResponseWithBooking getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
        if (!(item.getOwner().getId() == userId)) {
            return itemMapper.toItemWithNullBooking(item, null, null);
        }
        List<Booking> lastBookings = bookingRepository.findLastBookingsByItemIdOrderByStartDesc(itemId,
                LocalDateTime.now().withNano(0));
        ShortBookingDto lastBookingShort;
        if (lastBookings.isEmpty()) {
            lastBookingShort = null;
        } else {
            lastBookingShort = bookingMapper.toShortBooking(lastBookings.get(0));
        }

        List<Booking> nextBookings = bookingRepository.findNextBookingsByItemIdOrderByStartAsc(itemId,
                LocalDateTime.now().withNano(0));
        ShortBookingDto nextBookingShort;
        if(nextBookings.isEmpty()) {
            nextBookingShort = null;
        } else {
            nextBookingShort = bookingMapper.toShortBooking(nextBookings.get(0));
        }
        if (lastBookingShort == null || nextBookingShort == null) {
            return itemMapper.toItemWithNullBooking(item, lastBookingShort, nextBookingShort);
        }
            return itemMapper.toItemWithBooking(item, lastBookingShort, nextBookingShort);
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
            return commentRepository.save(comment);
        } else {
            throw new ValidationException("Вы не бронировали данную вещь " + itemId);
        }
    }

    @Override
    public List<Item> findAllByRequestIds(List<Integer> ids) {
        return itemRepository.findAllByRequestIds(ids);
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи нет " + itemId));
    }

    private void validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет "
                + userId));
    }
}

