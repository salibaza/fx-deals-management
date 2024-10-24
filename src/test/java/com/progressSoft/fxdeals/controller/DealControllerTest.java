package com.progressSoft.fxdeals.controller;

import com.progressSoft.fxdeals.exception.DealValidationException;
import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.service.DealProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealControllerTest {

    private DealProcessingService dealProcessingService;
    private DealController dealController;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        dealProcessingService = Mockito.mock(DealProcessingService.class);
        mockRequest = Mockito.mock(HttpServletRequest.class);
        dealController = new DealController(dealProcessingService);
    }

    @Test
    void shouldReturnCreatedStatusForValidDeals() {
        // Arrange
        List<Deal> validDeals = Arrays.asList(createValidDeal(), createValidDeal());

        // Act
        ResponseEntity<String> response = dealController.addDeals(validDeals, mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("processed successfully"));
    }

    @Test
    void shouldReturnBadRequestStatusForValidationErrors() {
        // Arrange
        List<Deal> deals = Arrays.asList(createValidDeal(), createInvalidDeal());
        doThrow(new DealValidationException("Invalid Deal")).when(dealProcessingService).processDeal(deals.get(1));

        // Act
        ResponseEntity<String> response = dealController.addDeals(deals, mockRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Error processing Deal"));
    }

    @Test
    void shouldReturnBadRequestStatusForUnexpectedErrors() {
        // Arrange
        List<Deal> deals = List.of(createValidDeal());
        doThrow(new RuntimeException("Unexpected error")).when(dealProcessingService).processDeal(deals.get(0));

        // Act
        ResponseEntity<String> response = dealController.addDeals(deals, mockRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Unexpected error"));
    }

    // Helper method to create a valid deal
    private Deal createValidDeal() {
        Deal deal = new Deal();
        deal.setUniqueId("123");
        deal.setFromCurrency("USD");
        deal.setToCurrency("EUR");
        deal.setDealAmount(BigDecimal.valueOf(1000.00));
        deal.setDealTimestamp(LocalDateTime.now());
        return deal;
    }

    // Helper method to create an invalid deal
    private Deal createInvalidDeal() {
        Deal deal = new Deal();
        deal.setUniqueId("456");
        deal.setFromCurrency("US");  // Invalid currency code (should be 3 letters)
        deal.setToCurrency("EURA");
        deal.setDealAmount(BigDecimal.valueOf(-100.00));  // Invalid deal amount (negative value)
        deal.setDealTimestamp(LocalDateTime.now());
        return deal;
    }
}