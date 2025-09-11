package com.example.bankcards.controller;

import com.example.bankcards.dto.CardNumberResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.PageResponse;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.security.UserPrincipal;
import com.example.bankcards.service.CardsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cards")
@CrossOrigin
@Tag(name = "Cards Controller", description = "Controller for card operations for authenticated users")
public class CardsController {

    private final CardsService cardsService;

    @Autowired
    public CardsController(CardsService cardsService) {
        this.cardsService = cardsService;
    }

    @Operation(
            summary = "Get card details",
            description = "Retrieve detailed information about a specific card owned by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card retrieved successfully",
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
                    description = "Access denied - card belongs to another user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"You do not have permission to access this card\", \"timestamp\": \"1757615223938\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCard(
            @Parameter(description = "ID of the card to retrieve", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return new ResponseEntity<>(cardsService.findOne(id, user.getUserId()), HttpStatus.OK);
    }

    @Operation(
            summary = "Request card blocking",
            description = "Request blocking of a specific card. The card status will be changed to BLOCK_REQUESTED for admin review"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Blocking request submitted successfully"
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
                    description = "Access denied - card belongs to another user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"You do not have permission to access this card\", \"timestamp\": \"1757615223938\"}"
                            )
                    )
            )
    })
    @PatchMapping("/block/{id}")
    public ResponseEntity<HttpStatus> requestCardBlocking(
            @Parameter(description = "ID of the card to block", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrincipal user
    ) {
        cardsService.requestBlocking(id, user.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Get card balance",
            description = "Retrieve the current balance of a specific card"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "1500.75"
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
                    description = "Access denied - card belongs to another user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"You do not have permission to access this card\", \"timestamp\": \"1757615223938\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "ID of the card to get balance for", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return new ResponseEntity<>(cardsService.getBalance(id, user.getUserId()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get full card number",
            description = "Retrieve the complete (decrypted) card number for a specific card. Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Card number retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CardNumberResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"cardNumber\": \"1234567812345678\"}"
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
                    description = "Access denied - card belongs to another user",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"You do not have permission to access this card\", \"timestamp\": \"1757615223938\"}"
                            )
                    )
            )
    })
    @GetMapping("/{id}/card-number")
    public ResponseEntity<CardNumberResponse> getCardNumber(
            @Parameter(description = "ID of the card to get number for", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return new ResponseEntity<>(cardsService.getCardNumber(id, user.getUserId()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get user cards with filtering",
            description = "Retrieve paginated list of user's cards with sorting and filtering options"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cards retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"content\": [{\"id\": 1, \"ownerId\": 2, " +
                                            "\"cardNumber\": \"**** **** **** 5678\", " +
                                            "\"expiryDate\": \"2029-09-11 15:05:03.693\", \"status\": \"ACTIVE\", " +
                                            "\"balance\": 673.24}], \"page\": 0, \"limit\": 10, \"totalElements\": 3, \"totalPages\": 1}"
                            )
                    )
            )
    })
    @GetMapping("/cards")
    public ResponseEntity<PageResponse<CardResponse>> getCards(
            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @Parameter(description = "Sorting criteria in format: property:direction. Multiple sorts supported")
            @RequestParam(value = "sort", defaultValue = "balance:desc") String[] sort,
            @Parameter(description = "Filter by card status", example = "ACTIVE")
            @RequestParam(value = "status", required = false) CardStatus cardStatus,
            @Parameter(description = "Minimum balance filter", example = "100.00")
            @RequestParam(value = "minBalance", required = false) BigDecimal minBalance,
            @Parameter(description = "Maximum balance filter", example = "5000.00")
            @RequestParam(value = "maxBalance", required = false) BigDecimal maxBalance,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserPrincipal user
    ) {
        PageResponse<CardResponse> pageResponse = cardsService.getUserCards(user.getUserId(), page, limit, sort, cardStatus, minBalance, maxBalance);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

}
