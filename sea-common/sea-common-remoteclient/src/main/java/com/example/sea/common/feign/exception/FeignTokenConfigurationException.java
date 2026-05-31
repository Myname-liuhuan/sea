package com.example.sea.common.feign.exception;

import java.util.List;

public class FeignTokenConfigurationException extends RuntimeException {

    private final List<String> missingFields;

    public FeignTokenConfigurationException(List<String> missingFields) {
        super("Feign token configuration is incomplete. Missing fields: " + String.join(", ", missingFields));
        this.missingFields = missingFields;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }
}