package com.progressSoft.fxdeals.controller;

import com.progressSoft.fxdeals.exception.RetrievalException;
import com.progressSoft.fxdeals.exception.DealValidationException;
import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.service.DealProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/deals") // All requests that start with /deals are handled by this controller
public class DealController {
    private final DealProcessingService dealProcessingService;

    public DealController(DealProcessingService dealProcessingService) {
        this.dealProcessingService = dealProcessingService;
    }
    private static final Logger logger = LoggerFactory.getLogger(DealController.class);

    @PostMapping
    public ResponseEntity<String> addDeals(@RequestBody List<Deal> deals, HttpServletRequest request) {
        StringBuilder result = new StringBuilder();
        boolean hasErrors = false;

        for (Deal deal : deals) {
            try {
                // Set the uniqueId in the request scope to be accessed by the exception handler
                setUniqueIdInRequestScope(request, deal.getUniqueId());

                // Validate and process the deal
                dealProcessingService.processDeal(deal);

                logger.debug("Deal processed successfully: {}", deal);

                // Append success message
                result.append("Deal with UniqueId ")
                        .append(deal.getUniqueId())
                        .append(" processed successfully.\n");

            } catch (DealValidationException e) {
                // Append validation error message
                result.append("Error processing Deal with UniqueId ")
                        .append(deal.getUniqueId())
                        .append(": ")
                        .append(e.getMessage())
                        .append("\n");
                hasErrors = true;

            } catch (Exception e) {
                // Append unexpected error message
                result.append("Unexpected error processing Deal with UniqueId ")
                        .append(deal.getUniqueId())
                        .append(": ")
                        .append(e.getMessage())
                        .append("\n");
                hasErrors = true;
            }
        }

        // Return 400 if there are validation errors, else 201 for successful creation
        if (hasErrors) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.toString());
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.toString());
        }
    }

    // Utility method to set uniqueId in request scope for error handling
    private void setUniqueIdInRequestScope(HttpServletRequest request, String uniqueId) {
        request.setAttribute("uniqueId", uniqueId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllDeals() {
        try {
            List<Deal> deals = dealProcessingService.getAllDeals();
            // If no deals are found, return 204 No Content
            if (deals.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            // Return 200 OK with the list of deals
            return ResponseEntity.ok(deals);

        } catch (Exception e) {
            throw new RetrievalException();
        }
    }
}