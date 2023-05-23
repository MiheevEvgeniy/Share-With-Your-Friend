package ru.practicum.shareit.exception;

public class EmailConflictException extends RuntimeException {
    public EmailConflictException(String message) {
        super(message);
    }
}
