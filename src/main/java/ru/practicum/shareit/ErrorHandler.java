package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({OwnerNotFoundException.class, EntityNotFoundException.class, ItemNotFoundException.class,
            ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final Exception e) {
        return new ErrorResponse(e.getMessage(), e.getStackTrace());
    }

    @ExceptionHandler({WrongOwnerException.class, InvalidEmailException.class, UnavailableItemException.class,
            InvalidBookingDurationException.class, InvalidCommentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final Exception e) {
        return new ErrorResponse(e.getMessage(), e.getStackTrace());
    }

    @ExceptionHandler({UnsupportedStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedStatusException(final UnsupportedStatusException e) {
        return new ErrorResponse(e.getMessage(), e.getStackTrace());
    }

    @ExceptionHandler({OwnerAccessException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final Exception e) {
        return new ErrorResponse(e.getMessage(), e.getStackTrace());
    }

    @ExceptionHandler({EmailConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailConflict(final Exception e) {
        return new ErrorResponse(e.getMessage(), e.getStackTrace());
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
