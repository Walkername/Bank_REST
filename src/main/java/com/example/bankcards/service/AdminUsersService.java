package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.exception.UserExistsException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminUsersService {

    private final UsersRepository usersRepository;

    public AdminUsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User findOne(int id) {
        Optional<User> user = usersRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        return user.get();
    }

    @Transactional
    public void changeUsername(int id, String username) {
        Optional<User> user = usersRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        boolean exists = usersRepository.existsByUsername(username);
        if (exists) {
            throw new UserExistsException("User with such username already exists");
        }
        user.get().setUsername(username);
    }

    @Transactional
    public void assignAdminRole(int id) {
        Optional<User> user = usersRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        user.get().setRole(Role.ADMIN);
    }

    public Optional<User> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

}
