package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b join fetch b.item where b.id = :bookingId")
    Optional<Booking> findById(@Param("bookingId") Long bookingId);

//    @Query("select b.id from Booking b")
//    List<Long> getBookingIds();

    @Query("select b from Booking b join fetch b.item where b.booker.id = :userId order by b.start desc")
    List<Booking> findBookingByBookerIdOrderByStartDesc(Long userId);

     Page<Booking> findBookingsByBookerIdOrderByStartDesc(PageRequest pageRequest, @Param("bookerId") Long bookerId);

    Page<Booking> findAllByItem_OwnerIdOrderByStartDesc(PageRequest pageRequest, @Param("ownerId") Long ownerId);

    @Query("select b from Booking b join fetch b.item where b.item.id = :itemId and b.end < :now order by b.start desc")
    List<Booking> findLastBookingsByItemIdOrderByStartDesc(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b join fetch b.item where b.item.id = :itemId and b.start > :now order by b.start asc")
    List<Booking> findNextBookingsByItemIdOrderByStartAsc(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b join fetch b.item where b.status = 'WAITING' and b.booker.id = :userId")
    List<Booking> findUserBookingsWaitingState(@Param("userId") Long userId);

    @Query("select b from Booking b join fetch b.item where b.status = 'REJECTED' and b.booker.id = :userId")
    List<Booking> findUserBookingsRejectedState(@Param("userId") Long userId);

    @Query("select b from Booking b join fetch b.item where b.booker.id = :userId and b.start < :now and b.end > :now")
    List<Booking> findUserBookingsCurrentState(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b join fetch b.item where b.booker.id = :userId and b.start > :now order by b.start desc")
    List<Booking> findFutureByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b join fetch b.item where b.booker.id = :userId and b.end < :now order by b.start desc")
    List<Booking> findPastByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);


    @Query("select b from Booking b join fetch b.item where b.item.owner.id = :ownerId order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(@Param("ownerId") Long ownerId);

    @Query("select b from Booking b join fetch b.item where b.item.owner.id = :ownerId and b.start > :now order by b.start desc")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);


    @Query("select b from Booking b join fetch b.item where b.item.owner.id = :ownerId and b.end < :now order by b.start desc")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b join fetch b.item where b.item.owner.id = :ownerId and b.start < :now and b.end > :now order by b.start desc")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("select b from Booking b join fetch b.item where b.status = :state and b.item.owner.id = :ownerId")
    List<Booking> findAllBookingsByOwnerIdAndStateIgnoreCase(@Param("state") Status state, Long ownerId);

    @Query("select b from Booking b join fetch b.item where b.booker.id = :userId and b.item.id = :itemId and b.status = :status and b.end < :time")
    List<Booking> findBookingByBookerAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId,
                                               @Param("status") Status status, @Param("time") LocalDateTime time);
}
