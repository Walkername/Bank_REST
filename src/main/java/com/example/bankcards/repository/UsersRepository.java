package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(@NotBlank(message = "Username should not be blank") String username);

    boolean existsByUsername(@NotBlank(message = "Username should not be blank") String username);
}
