package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.ItemResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.item.comments.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto toDto(Item item, Long userId, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (Objects.equals(item.getOwner().getId(), userId)) {
            if (lastBooking != null && lastBooking.getStatus() != BookingStatus.REJECTED) {
                ItemResponseBookingDto lastBookingDto = ItemResponseBookingDto.builder()
                        .id(lastBooking.getId())
                        .bookerId(lastBooking.getBooker().getId())
                        .status(lastBooking.getStatus())
                        .build();
                itemDto.setLastBooking(lastBookingDto);
            }
            if (nextBooking != null && nextBooking.getStatus() != BookingStatus.REJECTED) {
                ItemResponseBookingDto nextBookingDto = ItemResponseBookingDto.builder()
                        .id(nextBooking.getId())
                        .bookerId(nextBooking.getBooker().getId())
                        .status(nextBooking.getStatus())
                        .build();
                itemDto.setNextBooking(nextBookingDto);
            }
        }
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        itemDto.setComments(comments);
        return itemDto;
    }

    public ItemDtoForRequest toDtoForRequest(Item item) {
        return ItemDtoForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    public Item toEntity(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        if (itemDto != null) {
            Item item = new Item();
            item.setId(itemDto.getId());
            item.setDescription(itemDto.getDescription());
            item.setName(itemDto.getName());
            item.setAvailable(itemDto.getAvailable());
            item.setOwner(owner);
            item.setRequest(itemRequest);
            return item;
        }
        return null;
    }
}
