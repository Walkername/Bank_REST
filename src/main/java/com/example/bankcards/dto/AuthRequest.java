package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Authentication request DTO containing login/register credentials")
public class AuthRequest {

    @Schema(
            description = "Username for authentication",
            example = "john_doe",
            minLength = 3,
            maxLength = 50
    )
    @Size(min = 5, max = 64, message = "Username should be greater than 5 and less than 64")
    @NotBlank(message = "Username should be specified")
    private String username;

    @Schema(
            description = "Password for authentication",
            example = "securePassword123",
            minLength = 6,
            maxLength = 100
    )
    @Size(min = 5, max = 60, message = "Password should be greater than 5 and less than 60")
    @NotBlank(message = "Password should be specified")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
