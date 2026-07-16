package com.github.bovvver.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyVerifiedException.class)
    public ResponseEntity<String> handleAlreadyVerifiedException(AlreadyVerifiedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(URLGenerationFailedException.class)
    public ResponseEntity<String> handleURLGenerationFailedException(URLGenerationFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(EmptyVerificationDataException.class)
    public ResponseEntity<String> handleEmptyVerificationDataException(EmptyVerificationDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(VerificationNotFoundException.class)
    public ResponseEntity<String> handleVerificationNotFoundException(VerificationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}


