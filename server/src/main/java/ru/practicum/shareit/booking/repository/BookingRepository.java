package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //Query for ALL, REJECTED, WAITING, APPROVED
    List<Booking> findByStatusInOrderByStartDesc(List<BookingStatus> status);

    //Query for PAST
    List<Booking> findByEndBeforeOrderByStartDesc(LocalDateTime before);

    //Query for CURRENT
    List<Booking> findByStartBeforeAndEndAfterOrderByStartDesc(LocalDateTime before, LocalDateTime after);

    //Query for FUTURE
    List<Booking> findByStartAfterOrderByStartDesc(LocalDateTime after);

    //get last booking
    Booking findFirstByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime before);

    //get next booking
    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime before);

}
