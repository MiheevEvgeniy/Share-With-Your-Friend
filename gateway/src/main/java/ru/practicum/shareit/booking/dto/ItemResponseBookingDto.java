package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.enums.BookingStatus;

@Data
@Builder
public class ItemResponseBookingDto {

    private Long id;

    private Long bookerId;

    private BookingStatus status;

}

