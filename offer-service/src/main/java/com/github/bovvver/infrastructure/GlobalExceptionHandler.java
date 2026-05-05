package com.github.bovvver.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<String> handleOfferNotFoundException(OfferNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedExecutorException.class)
    public ResponseEntity<String> handleUnauthorizedExecutorException(UnauthorizedExecutorException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(OperationNotAllowedInCurrentStateException.class)
    public ResponseEntity<String> handleOperationNotAllowedInCurrentStateException(OperationNotAllowedInCurrentStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}

