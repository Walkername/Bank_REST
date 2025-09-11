package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.ChangeUsernameRequest;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.service.CardsService;
import com.example.bankcards.service.AdminUsersService;
import com.example.bankcards.util.BankModelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin
@Tag(name = "Admin Users Controller", description = "Admin Controller to control users")
public class AdminUsersController {

    private final CardsService cardsService;
    private final AdminUsersService adminUsersService;
    private final BankModelMapper bankModelMapper;

    @Autowired
    public AdminUsersController(CardsService cardsService, AdminUsersService adminUsersService, BankModelMapper bankModelMapper) {
        this.cardsService = cardsService;
        this.adminUsersService = adminUsersService;
        this.bankModelMapper = bankModelMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Long id
    ) {
        UserResponse userResponse = bankModelMapper.convertToUserResponse(adminUsersService.findOne(id));
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<HttpStatus> assignAdminRole(
            @PathVariable("id") Long id
    ) {
        adminUsersService.assignAdminRole(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{id}/username")
    public ResponseEntity<HttpStatus> changeUsername(
            @PathVariable("id") Long id,
            @RequestBody @Valid ChangeUsernameRequest request
    ) {
        adminUsersService.changeUsername(id, request.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Get user cards", description = "Get list of user cards with pagination, sorting and filtration")
    @GetMapping("/{userId}/cards")
    public ResponseEntity<PageResponse<CardResponse>> getUserCards(
            @PathVariable Long userId,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "sort", defaultValue = "balance:desc", required = false) String[] sort,
            @RequestParam(value = "status", required = false) CardStatus cardStatus,
            @RequestParam(value = "minBalance", required = false) BigDecimal minBalance,
            @RequestParam(value = "maxBalance", required = false) BigDecimal maxBalance
    ) {
        PageResponse<CardResponse> pageResponse = cardsService.getUserCards(userId, page, limit, sort, cardStatus, minBalance, maxBalance);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

}
