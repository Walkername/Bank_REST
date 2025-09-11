package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeUsernameRequest {

    @NotBlank(message = "Username should not be blank")
    @Size(min = 5, max = 64, message = "Username should be greater than 5 and less than 64")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
