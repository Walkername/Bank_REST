package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for changing username")
public class ChangeUsernameRequest {

    @Schema(
            description = "New username",
            example = "new_username",
            minLength = 5,
            maxLength = 64
    )
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, max = 64, message = "Username should be greater than 5 and less than 64")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
