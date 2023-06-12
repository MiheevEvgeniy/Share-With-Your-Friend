package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.enums.BookingStatus;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingInputDto {

    private Long id;

    private Long bookerId;

    private Long itemId;

    private BookingStatus status;
    @FutureOrPresent
    @NotNull
    private LocalDateTime start;
    @FutureOrPresent
    @NotNull
    private LocalDateTime end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookingInputDto)) return false;
        return id != null && id.equals(((BookingInputDto) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

