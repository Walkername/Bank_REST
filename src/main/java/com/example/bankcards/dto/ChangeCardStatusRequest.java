package com.example.bankcards.dto;

import jakarta.validation.constraints.Pattern;

public class ChangeCardStatusRequest {

    @Pattern(regexp = "ACTIVE|BLOCKED", message = "You can use only ACTIVE or BLOCKED")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
