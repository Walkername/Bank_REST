package com.example.bankcards.controller;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.ChangeCardStatusRequest;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.WrongCardStatus;
import com.example.bankcards.service.AdminCardsService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/cards")
@CrossOrigin
@Tag(name = "Admin Cards Controller", description = "Admin Controller for CRUD operations with bank cards")
public class AdminCardsController {
    private final AdminCardsService adminCardsService;

    @Autowired
    public AdminCardsController(AdminCardsService adminCardsService) {
        this.adminCardsService = adminCardsService;
    }

    @Operation(
            summary = "Get any card",
            description = "Retrieves detailed information about a specific card by its ID. Admin privileges required."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"id\": 1, \"ownerId\": 2, \"cardNumber\": \"**** **** **** 5678\", " +
                                            "\"expiryDate\": \"2029-09-11 15:05:03.693\", \"status\": \"ACTIVE\", " +
                                            "\"balance\": 174.88}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Card not found\", \"timestamp\": \"1757615223938\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - admin privileges required"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> get(
            @Parameter(description = "ID of the card to retrieve", required = true, example = "1")
            @PathVariable("id") Long id
    ) {
        return new ResponseEntity<>(adminCardsService.findOne(id), HttpStatus.OK);
    }


    @Operation(
            summary = "Create new card",
            description = "Creates a new bank card for a specific user. Generates card number, expiry date automatically."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Card created successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"User not Found\", \"timestamp\": \"1757615790252\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - admin privileges required"
            )
    })
    @PostMapping("/create/{userId}")
    public ResponseEntity<HttpStatus> create(
            @Parameter(description = "ID of the user for whom the card is being created", required = true, example = "5")
            @PathVariable Long userId
    ) {
        adminCardsService.create(userId);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @Operation(
            summary = "Change card status",
            description = "Updates the status of a specific card. " +
                    "Cannot change status to EXPIRED or BLOCK_REQUESTED directly."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status change attempt. Trying to assign as EXPIRED or BLOCK_REQUESTEd",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"You can change card status only to ACTIVE or BLOCKED\", " +
                                            "\"timestamp\": \"1757615790252\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Card not Found\", \"timestamp\": \"1757615790252\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - admin privileges required"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Card status change request",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChangeCardStatusRequest.class),
                    examples = @ExampleObject(
                            value = "{\"status\": \"BLOCKED\"}"
                    )
            )
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<HttpStatus> changeStatus(
            @Parameter(description = "ID of the card to update", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody @Valid ChangeCardStatusRequest request
    ) {
        CardStatus status = CardStatus.valueOf(request.getStatus());
        if (status.equals(CardStatus.EXPIRED) || status.equals(CardStatus.BLOCKED)) {
            throw new WrongCardStatus("You can change card status only to ACTIVE or BLOCKED");
        }
        adminCardsService.setStatus(id, status);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Operation(
            summary = "Delete card",
            description = "Permanently deletes a specific card from the system. This action cannot be undone."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Card deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Card not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Card not Found\", \"timestamp\": \"1757615790252\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - admin privileges required"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(
            @Parameter(description = "ID of the card to delete", required = true, example = "1")
            @PathVariable("id") Long id
    ) {
        adminCardsService.deleteCard(id);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

}
