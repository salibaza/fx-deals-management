package com.progressSoft.fxdeals.service;

import com.progressSoft.fxdeals.exception.DealValidationException;
import com.progressSoft.fxdeals.model.Deal;
import org.springframework.stereotype.Service;

@Service
public class DealValidationService {

    private final CurrencyValidationService currencyValidationService;

    public DealValidationService(CurrencyValidationService currencyValidationService) {
        this.currencyValidationService = currencyValidationService;
    }

    public void validateDeal(Deal deal) throws DealValidationException {
        StringBuilder errors = new StringBuilder();

        // Validate Mandatory Fields
        if (deal.getUniqueId() == null || deal.getUniqueId().isEmpty()) {
            errors.append("Deal Unique ID is required. ");
        }
        if (deal.getFromCurrency() == null || deal.getFromCurrency().isEmpty()) {
            errors.append("From Currency is required. ");
        }
        if (deal.getToCurrency() == null || deal.getToCurrency().isEmpty()) {
            errors.append("To Currency is required. ");
        }
        if (deal.getDealTimestamp() == null) {
            errors.append("Deal Timestamp is required. ");
        }
        if (deal.getDealAmount() == null || deal.getDealAmount().doubleValue() < 0) {
            errors.append("Deal Amount is required and must be a positive number.");
        }

        // If there are errors, throw a ValidationException with all error messages
        if (errors.length() > 0) {
            throw new DealValidationException(errors.toString().trim());
        }

        // Validate From and To currencies using CurrencyValidationService
        currencyValidationService.validateCurrencyCode(deal.getFromCurrency(), "From");
        currencyValidationService.validateCurrencyCode(deal.getToCurrency(), "To");
    }
}
