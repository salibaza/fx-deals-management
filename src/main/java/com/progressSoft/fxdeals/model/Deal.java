package com.progressSoft.fxdeals.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Deal {
    @Id  // This makes uniqueId the primary key
    @Column(unique = true, nullable = false)  // Ensures unique and non-null values
    private String uniqueId;

    @Column(nullable = false)
    private String fromCurrency;

    @Column(nullable = false)
    private String toCurrency;

    @Column(nullable = false)
    private LocalDateTime dealTimestamp;

    @Column(nullable = false)
    private BigDecimal dealAmount;
}
