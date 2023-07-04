package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
