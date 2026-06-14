package com.github.bovvver.bookingmanagement.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingDecisionValidationException.class)
    public ResponseEntity<String> handleBookingDecisionValidationException(BookingDecisionValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleBookingNotFoundException(BookingNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PositionNotFoundException.class)
    public ResponseEntity<String> handlePositionNotFoundException(PositionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OperationNotAllowedInCurrentStateException.class)
    public ResponseEntity<String> handleOperationNotAllowedInCurrentStateException(OperationNotAllowedInCurrentStateException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OfferOwnershipException.class)
    public ResponseEntity<String> handleOfferOwnershipException(OfferOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(BookingOwnershipException.class)
    public ResponseEntity<String> handleBookingOwnershipException(BookingOwnershipException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(OutdatedNegotiationPositionException.class)
    public ResponseEntity<String> handleOutdatedPositionException(OutdatedNegotiationPositionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(EventPublicationFailedException.class)
    public ResponseEntity<String> handleEventPublicationFailedException(EventPublicationFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(BookingNotExpiredYetException.class)
    public ResponseEntity<String> handleBookingNotExpiredYetException(BookingNotExpiredYetException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
