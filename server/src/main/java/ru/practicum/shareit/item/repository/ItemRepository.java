package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select i.id from Item i")
    List<Long> getItemsIds();

    @Query(value = "select i from Item i where i.owner.id = ?1 order by i.id")
    List<Item> getUserItemsOrderedById(Long userId);

    List<Item> findByNameOrDescriptionContainingIgnoreCase(String text1, String text2);

    @Query(value = "select i from Item i where i.request.id = :requestId")
    List<Item> findItemByRequestId(@Param("requestId") Integer requestId);

    @Query(value = "select i from Item i where i.request.id = :requestIds")
    List<Item> findAllByRequestIds(@Param("requestIds") List<Integer> requestIds);

    @Query("select i from Item i where i.id = :itemId")
    Optional<Item> findById(@Param("itemId") Long itemId);
}
