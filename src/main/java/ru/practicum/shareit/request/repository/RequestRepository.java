package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select r from ItemRequest r where r.requester.id =:userId")
    List<ItemRequest> getItemRequestByUserId(@Param("userId") long userId);

    Page<ItemRequest> findItemRequestByRequester_IdIsNot(PageRequest pageRequest, @Param("userId") long userId);

    @Query("select r from ItemRequest r where r.requester.id =:userId")
    Optional<ItemRequest> findById(@Param("userId") long userId);

    @Query("select r from ItemRequest r where r.requester = :requestor")
    List<ItemRequest> findAllByRequestor(@Param("requestor") User requestor);
}
