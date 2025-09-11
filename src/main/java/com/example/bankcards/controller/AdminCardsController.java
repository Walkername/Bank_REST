package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.ChangeCardStatusRequest;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.WrongCardStatus;
import com.example.bankcards.service.AdminCardsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cards")
@CrossOrigin
@Tag(name = "Admin Cards Controller", description = "Admin Controller for CRUD operations with cards")
public class AdminCardsController {
    private final AdminCardsService adminCardsService;

    @Autowired
    public AdminCardsController(AdminCardsService adminCardsService) {
        this.adminCardsService = adminCardsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> get(
            @PathVariable("id") Long id
    ) {
        return new ResponseEntity<>(adminCardsService.findOne(id), HttpStatus.OK);
    }

    @PostMapping("/create/{userId}")
    public ResponseEntity<HttpStatus> create(
            @PathVariable Long userId
    ) {
        adminCardsService.create(userId);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HttpStatus> changeStatus(
            @PathVariable("id") Long id,
            @RequestBody @Valid ChangeCardStatusRequest request
    ) {
        CardStatus status = CardStatus.valueOf(request.getStatus());
        if (status.equals(CardStatus.EXPIRED)) {
            throw new WrongCardStatus("You can't change card status to expired");
        }
        adminCardsService.setStatus(id, status);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(
            @PathVariable("id") Long id
    ) {
        adminCardsService.deleteCard(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
