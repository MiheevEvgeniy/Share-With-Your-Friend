package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.OwnerAccessException;
import ru.practicum.shareit.exception.OwnerNotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({OwnerAccessException.class, OwnerNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOwnerException(final Exception e) {
        return new ErrorResponse("Owner error " + e.getMessage(), e.getStackTrace());
    }

    @ExceptionHandler({InvalidEmailException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidEmailException(final Exception e) {
        return new ErrorResponse("Email not found " + e.getMessage(), e.getStackTrace());
    }

    @ExceptionHandler({EmailConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailConflict(final Exception e) {
        return new ErrorResponse("Email conflict error " + e.getMessage(), e.getStackTrace());
    }

    private static class ErrorResponse {
        String error;
        StackTraceElement[] stackTrace;

        ErrorResponse(String error, StackTraceElement[] stackTrace) {
            this.error = error;
            this.stackTrace = stackTrace;
        }

        public String getError() {
            return error;
        }

        public StackTraceElement[] getStackTrace() {
            return stackTrace;
        }
    }
}
