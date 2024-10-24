package com.progressSoft.fxdeals.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle validation errors
    @ExceptionHandler(DealValidationException.class)
    public ResponseEntity<String> handleValidationException(DealValidationException ex, WebRequest request) {
        String uniqueId = (String) request.getAttribute("uniqueId", RequestAttributes.SCOPE_REQUEST);
        String errorMessage;

        if (uniqueId != null) {
            errorMessage = "Error processing Deal with UniqueId " + uniqueId + ": " + ex.getMessage();
            logger.error("Validation error for Deal with UniqueId {}: {}", uniqueId, ex.getMessage());
        } else {
            errorMessage = "Error processing Deal: " + ex.getMessage();
            logger.error("Validation error for Deal: {}", ex.getMessage());
        }

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    // Handle retrieval errors
    @ExceptionHandler(RetrievalException.class)
    public ResponseEntity<String> handleRetrievalException(RetrievalException ex) {
        logger.error("An error occurred while retrieving the deals.", ex);

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle other general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        String uniqueId = (String) request.getAttribute("uniqueId", RequestAttributes.SCOPE_REQUEST);
        String errorMessage = (uniqueId != null) ?
                "Unexpected error for Deal with UniqueId " + uniqueId + ": " + ex.getMessage() :
                "An unexpected error occurred: " + ex.getMessage();

        logger.error("An unexpected error occurred", ex);

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}