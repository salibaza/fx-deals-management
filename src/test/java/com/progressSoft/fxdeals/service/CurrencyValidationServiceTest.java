package com.progressSoft.fxdeals.service;

import com.progressSoft.fxdeals.exception.DealValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyValidationServiceTest {

    private CurrencyValidationService currencyValidationService;

    @BeforeEach
    void setUp() {
        currencyValidationService = new CurrencyValidationService();
    }

    @Test
    void shouldThrowExceptionForInvalidFromCurrencyCodeFormat() {
        DealValidationException exception = assertThrows(DealValidationException.class,
                () -> currencyValidationService.validateCurrencyCode("US", "From"));

        assertEquals("Invalid From Currency format. Must be a 3-letter ISO code.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidToCurrencyCodeFormat() {
        DealValidationException exception = assertThrows(DealValidationException.class,
                () -> currencyValidationService.validateCurrencyCode("EURA", "To"));

        assertEquals("Invalid To Currency format. Must be a 3-letter ISO code.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNonExistentFromCurrencyCode() {
        DealValidationException exception = assertThrows(DealValidationException.class,
                () -> currencyValidationService.validateCurrencyCode("ABC", "From"));

        assertEquals("Invalid From Currency. Not a valid ISO 4217 currency code.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNonExistentToCurrencyCode() {
        DealValidationException exception = assertThrows(DealValidationException.class,
                () -> currencyValidationService.validateCurrencyCode("XYZ", "To"));

        assertEquals("Invalid To Currency. Not a valid ISO 4217 currency code.", exception.getMessage());
    }

    @Test
    void shouldPassForValidCurrencyCode() {
        assertDoesNotThrow(() -> currencyValidationService.validateCurrencyCode("USD", "From"));
        assertDoesNotThrow(() -> currencyValidationService.validateCurrencyCode("EUR", "To"));
    }
}