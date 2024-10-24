package com.progressSoft.fxdeals.exception;

public class RetrievalException extends RuntimeException {
    public RetrievalException() {
        super("An error occurred while retrieving the deals.");
    }
}