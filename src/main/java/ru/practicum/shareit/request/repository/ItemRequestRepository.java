package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByOwnerOrderByCreatedDesc(User owner);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.owner.id != ?1")
    List<ItemRequest> findByRequester_IdNot(Long id, Pageable pageable);
}
