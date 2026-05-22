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

    @ExceptionHandler(UnauthorizedParticipantException.class)
    public ResponseEntity<String> handleUnauthorizedExecutorException(UnauthorizedParticipantException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(OperationNotAllowedInCurrentStateException.class)
    public ResponseEntity<String> handleOperationNotAllowedInCurrentStateException(OperationNotAllowedInCurrentStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CompletionProofRequiredException.class)
    public ResponseEntity<String> handleCompletionProofRequiredException(CompletionProofRequiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(URLGenerationFailedException.class)
    public ResponseEntity<String> handleURLGenerationFailedException(URLGenerationFailedException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}

