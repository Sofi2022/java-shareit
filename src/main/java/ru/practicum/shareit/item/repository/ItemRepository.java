package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select i.id from Item i")
    List<Long> getItemsIds();

    @Query(value = "select i from Item i where i.owner.id = ?1")
    List<Item> getUserItems(Long userId);

    List<Item> findByNameOrDescriptionContainingIgnoreCase(String text1, String text2);
}
