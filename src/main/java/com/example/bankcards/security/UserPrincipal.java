package com.example.bankcards.security;

public class UserPrincipal {

    private final int userId;

    private final String username;

    private final String role;

    public UserPrincipal(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
