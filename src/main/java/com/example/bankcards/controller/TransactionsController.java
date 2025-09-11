package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionRequest;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.TransactionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@CrossOrigin
@Tag(name = "Transactions Controller", description = "Controller for financial transactions between user's cards")
public class TransactionsController {

    private final TransactionsService transactionsService;

    @Autowired
    public TransactionsController(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @Operation(
            summary = "Transfer funds between cards",
            description = "Transfer funds between two cards owned by the same authenticated user. Both cards must belong to the user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid amount",
                                            value = "{\"error\": \"Amount must be greater than or equal to 0.01\", \"timestamp\": \"1757615790252\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Invalid amount",
                                            value = "{\"error\": \"Amount must have at most 2 decimal places\", \"timestamp\": \"1757615790252\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Same card transfer",
                                            value = "{\"error\": \"Transfer from the card to itself is not possible\", \"timestamp\": \"1757615790252\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Card not found\", \"timestamp\": \"1757615790252\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - cards belong to different users",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"You do not have permission to transfer with these cards\", \"timestamp\": \"1757615790252\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "402",
                    description = "Insufficient funds",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Insufficient funds\", \"timestamp\": \"1757615790252\"}"
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Transaction request details",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TransactionRequest.class),
                    examples = @ExampleObject(
                            value = "{\"fromCard\": \"1234567812345678\", " +
                                    "\"toCard\": \"8765432187654321\", \"amount\": 100.50}"
                    )
            )
    )
    @PatchMapping("/transfers")
    public ResponseEntity<HttpStatus> transfer(
            @RequestBody @Valid TransactionRequest transactionRequest,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrincipal user
    ) {
        transactionsService.transfer(transactionRequest, user.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
