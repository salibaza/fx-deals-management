package com.progressSoft.fxdeals.controller;

import com.progressSoft.fxdeals.exception.ValidationException;
import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.repository.DealRepository;
import com.progressSoft.fxdeals.service.DealValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deals") // All requests that start with /deals are handled by this controller
public class DealController {

    private final DealRepository dealRepository;

    private final DealValidationService dealValidationService;

    public DealController(DealRepository dealRepository, DealValidationService dealValidationService) {
        this.dealRepository = dealRepository;
        this.dealValidationService = dealValidationService;
    }

    @PostMapping("/batch") // POST requests to /deals/batch will be handled by this method
    public ResponseEntity<String> addDeals(@RequestBody List<Deal> deals) {
        StringBuilder result = new StringBuilder();

        for (Deal deal : deals) {
            try {
                // Validate the deal
                dealValidationService.validateDeal(deal);

                // Save the deal if validation passes
                dealRepository.save(deal);

                result.append("Deal with UniqueId ")
                        .append(deal.getUniqueId())
                        .append(" processed successfully.\n");
            } catch (ValidationException e) {
                // Handle invalid deal
                result.append("Error processing Deal with UniqueId ")
                        .append(deal.getUniqueId())
                        .append(": ")
                        .append(e.getMessage())
                        .append("\n");
            } catch (Exception e) {
                // Handle other unexpected errors
                result.append("Unexpected error for Deal with UniqueId ")
                        .append(deal.getUniqueId())
                        .append(": ")
                        .append(e.getMessage())
                        .append("\n");
            }
        }

        return ResponseEntity.ok(result.toString());
    }
}