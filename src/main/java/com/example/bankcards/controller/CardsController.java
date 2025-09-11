package com.example.bankcards.controller;

import com.example.bankcards.dto.CardNumberResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.CardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cards")
@CrossOrigin
@Tag(name = "Cards Controller", description = "Cards controller for ordinary users")
public class CardsController {

    private final CardsService cardsService;

    @Autowired
    public CardsController(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return new ResponseEntity<>(cardsService.findOne(id, user.getUserId()), HttpStatus.OK);
    }

    @PatchMapping("/block/{id}")
    public ResponseEntity<String> blockCard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        cardsService.requestBlocking(id, user.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return new ResponseEntity<>(cardsService.getBalance(id, user.getUserId()), HttpStatus.OK);
    }

    @GetMapping("/{id}/card-number")
    public ResponseEntity<CardNumberResponse> getCardNumber(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return new ResponseEntity<>(cardsService.getCardNumber(id, user.getUserId()), HttpStatus.OK);
    }

    @Operation(summary = "Get user cards", description = "Get list of user cards with pagination, sorting and filtration")
    @GetMapping("/cards")
    public ResponseEntity<PageResponse<CardResponse>> getCards(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @RequestParam(value = "sort", defaultValue = "balance:desc", required = false) String[] sort,
            @RequestParam(value = "status", required = false) CardStatus cardStatus,
            @RequestParam(value = "minBalance", required = false) BigDecimal minBalance,
            @RequestParam(value = "maxBalance", required = false) BigDecimal maxBalance,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        PageResponse<CardResponse> pageResponse = cardsService.getUserCards(user.getUserId(), page, limit, sort, cardStatus, minBalance, maxBalance);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

}
