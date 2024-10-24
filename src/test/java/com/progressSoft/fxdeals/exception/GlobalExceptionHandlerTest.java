package com.progressSoft.fxdeals.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnBadRequestForValidationException() {
        // Arrange
        DealValidationException ex = new DealValidationException("Invalid currency code");
        WebRequest mockRequest = Mockito.mock(WebRequest.class);

        // Simulate the uniqueId being set in the request attributes
        when(mockRequest.getAttribute("uniqueId", RequestAttributes.SCOPE_REQUEST)).thenReturn("deal123");

        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleValidationException(ex, mockRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error processing Deal with UniqueId deal123: Invalid currency code", response.getBody());

        // Verify the request was used properly
        verify(mockRequest, times(1)).getAttribute("uniqueId", RequestAttributes.SCOPE_REQUEST);
    }

    @Test
    void shouldReturnInternalServerErrorForGlobalException() {
        // Arrange
        Exception ex = new Exception("Something went wrong");
        WebRequest mockRequest = mock(WebRequest.class);

        // Simulate the WebRequest attribute for "uniqueId" being null
        when(mockRequest.getAttribute("uniqueId", WebRequest.SCOPE_REQUEST)).thenReturn(null);

        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleGlobalException(ex, mockRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred: Something went wrong", response.getBody());
    }

    @Test
    void shouldHandleRetrievalExceptionProperly() {
        // Arrange
        RetrievalException ex = new RetrievalException();

        // Act
        ResponseEntity<String> response = globalExceptionHandler.handleRetrievalException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred while retrieving the deals.", response.getBody());
    }
}