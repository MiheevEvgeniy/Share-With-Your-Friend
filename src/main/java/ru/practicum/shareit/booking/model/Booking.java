package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.enums.BookingStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking", schema = "public")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookerId;

    private Long itemId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && id.equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
