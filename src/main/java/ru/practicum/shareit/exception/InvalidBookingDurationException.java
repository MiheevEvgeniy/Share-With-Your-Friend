package ru.practicum.shareit.exception;

public class InvalidBookingDurationException extends RuntimeException {
    public InvalidBookingDurationException(String message) {
        super(message);
    }
}
