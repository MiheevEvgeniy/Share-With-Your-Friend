package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.comments.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto toDto(Item item, Long userId) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (Objects.equals(item.getOwnerId(), userId)) {
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
            if (lastBooking != null && lastBooking.getStatus() != BookingStatus.REJECTED) {
                itemDto.setLastBooking(lastBooking);
            }
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
            if (nextBooking != null && nextBooking.getStatus() != BookingStatus.REJECTED) {
                itemDto.setNextBooking(nextBooking);
            }
        }
        System.out.println(commentRepository.findByItemId(item.getId()));
        itemDto.setComments(commentRepository.findByItemId(item.getId()));
        return itemDto;
    }

    public Item toEntity(ItemDto itemDto, long ownerId) {
        if (itemDto != null) {
            Item item = new Item();
            item.setId(itemDto.getId());
            item.setDescription(itemDto.getDescription());
            item.setName(itemDto.getName());
            item.setAvailable(itemDto.getAvailable());
            item.setOwnerId(ownerId);
            return item;
        }
        return null;
    }
}
