package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request DTO for changing card status")
public class ChangeCardStatusRequest {

    @Schema(
            description = "New status for the card",
            example = "BLOCKED",
            allowableValues = {"ACTIVE", "BLOCKED"}
    )
    @Pattern(regexp = "ACTIVE|BLOCKED", message = "You can assign only ACTIVE or BLOCKED")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
