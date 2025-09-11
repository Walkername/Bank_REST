package com.example.bankcards.entity;

import com.example.bankcards.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "user_profile")
@Schema(name = "User", description = "User entity representing a system user with authentication credentials")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the user", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Username should not be blank")
    @Size(min = 5, max = 64, message = "Username should be greater than 5 and less than 64")
    @Column(name = "username")
    @Schema(
            description = "Unique username for authentication",
            example = "john_doe",
            minLength = 5,
            maxLength = 64
    )
    private String username;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 5, max = 60, message = "Password should be greater than 5 and less than 60")
    @Column(name = "password")
    @Schema(
            description = "Encrypted password for authentication",
            example = "$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            minLength = 5,
            maxLength = 60,
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Schema(
            description = "User role defining access permissions",
            example = "USER",
            allowableValues = {"USER", "ADMIN"},
            defaultValue = "USER"
    )
    private Role role;

    @OneToMany(mappedBy = "owner")
    @Schema(description = "List of cards owned by the user", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Card> cards;

    public User() {
    }

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
