package ru.practicum.shareit.exception;

public class OwnerAccessException extends RuntimeException {
    public OwnerAccessException(String message) {
        super(message);
    }
}
