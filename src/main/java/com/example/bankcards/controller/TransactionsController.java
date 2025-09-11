package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.TransactionsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@CrossOrigin
@Tag(name = "Transactions Controller")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @PatchMapping("/transfers")
    public ResponseEntity<HttpStatus> transfer(
            @RequestBody @Valid TransactionRequest transactionRequest,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        transactionsService.transfer(transactionRequest, user.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
