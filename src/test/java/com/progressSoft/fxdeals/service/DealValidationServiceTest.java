package com.progressSoft.fxdeals.service;

import com.progressSoft.fxdeals.exception.DealValidationException;
import com.progressSoft.fxdeals.model.Deal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DealValidationServiceTest {

    private DealValidationService dealValidationService;
    private CurrencyValidationService currencyValidationService;

    @BeforeEach
    void setUp() {
        currencyValidationService = Mockito.mock(CurrencyValidationService.class);
        dealValidationService = new DealValidationService(currencyValidationService);
    }

    @Test
    void shouldPassValidationForValidDeal() {
        Deal validDeal = createValidDeal();

        // Simulate that the currency codes are valid
        doNothing().when(currencyValidationService).validateCurrencyCode("USD", "From");
        doNothing().when(currencyValidationService).validateCurrencyCode("EUR", "To");

        // No exception should be thrown
        assertDoesNotThrow(() -> dealValidationService.validateDeal(validDeal));

        // Verify that currency validation is called correctly
        verify(currencyValidationService).validateCurrencyCode("USD", "From");
        verify(currencyValidationService).validateCurrencyCode("EUR", "To");
    }

    @Test
    void shouldThrowExceptionForMissingUniqueId() {
        Deal invalidDeal = createValidDeal();
        invalidDeal.setUniqueId(null);  // Set invalid unique ID

        DealValidationException exception = assertThrows(DealValidationException.class, () -> dealValidationService.validateDeal(invalidDeal));

        assertEquals("Deal Unique ID is required.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidDealAmount() {
        Deal invalidDeal = createValidDeal();
        invalidDeal.setDealAmount(BigDecimal.valueOf(-100.00));  // Invalid deal amount (negative value)

        DealValidationException exception = assertThrows(DealValidationException.class, () -> dealValidationService.validateDeal(invalidDeal));

        assertEquals("Deal Amount is required and must be a positive number.", exception.getMessage());
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
}