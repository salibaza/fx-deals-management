package com.progressSoft.fxdeals.controller;

import com.progressSoft.fxdeals.model.Deal;
import com.progressSoft.fxdeals.service.DealProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DealControllerRetrievalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealProcessingService dealProcessingService;

    @Test
    void shouldReturnAllDealsWith200Ok() throws Exception {
        // Arrange
        Deal deal1 = new Deal();
        deal1.setUniqueId("deal123");
        deal1.setFromCurrency("USD");
        deal1.setToCurrency("EUR");
        deal1.setDealAmount(BigDecimal.valueOf(1000.00));
        deal1.setDealTimestamp(LocalDateTime.now());

        Deal deal2 = new Deal();
        deal2.setUniqueId("deal124");
        deal2.setFromCurrency("GBP");
        deal2.setToCurrency("USD");
        deal2.setDealAmount(BigDecimal.valueOf(2000.00));
        deal2.setDealTimestamp(LocalDateTime.now());

        when(dealProcessingService.getAllDeals()).thenReturn(List.of(deal1, deal2));

        // Act & Assert
        mockMvc.perform(get("/api/deals")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uniqueId").value("deal123"))
                .andExpect(jsonPath("$[1].uniqueId").value("deal124"));
    }

    @Test
    void shouldReturnNoContentWhenNoDealsFound() throws Exception {
        // Arrange
        when(dealProcessingService.getAllDeals()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/deals")
                        .contentType("application/json"))
                .andExpect(status().isNoContent());
    }
}
