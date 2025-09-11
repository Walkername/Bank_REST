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

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin/users")
@CrossOrigin
@Tag(name = "Admin Users Controller", description = "Admin Controller for user management operations")
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

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves detailed information about a specific user by their ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"id\": 1, \"username\": \"john_doe\", \"role\": \"USER\"}"
                            )
                    )
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
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "ID of the user to retrieve", required = true, example = "1")
            @PathVariable Long id
    ) {
        UserResponse userResponse = bankModelMapper.convertToUserResponse(adminUsersService.findOne(id));
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @Operation(
            summary = "Assign admin role",
            description = "Assigns administrator role to a specific user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Admin role assigned successfully"
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
    @PatchMapping("/{id}/role")
    public ResponseEntity<HttpStatus> assignAdminRole(
            @Parameter(description = "ID of the user to assign admin role", required = true, example = "1")
            @PathVariable("id") Long id
    ) {
        adminUsersService.assignAdminRole(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Change username",
            description = "Changes the username of a specific user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Username changed successfully"
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
                    responseCode = "409",
                    description = "Username already exists",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "{\"error\": \"User with such username already exists\", \"timestamp\": \"1757615790252\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - admin privileges required"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New username request",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChangeUsernameRequest.class),
                    examples = @ExampleObject(
                            value = "{\"username\": \"new_username\"}"
                    )
            )
    )
    @PatchMapping("/{id}/username")
    public ResponseEntity<HttpStatus> changeUsername(
            @Parameter(description = "ID of the user to change username", required = true, example = "1")
            @PathVariable("id") Long id,
            @RequestBody @Valid ChangeUsernameRequest request
    ) {
        adminUsersService.changeUsername(id, request.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Get user cards",
            description = "Retrieves paginated list of user cards with sorting and filtering options"
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - admin privileges required"
            )
    })
    @GetMapping("/{userId}/cards")
    public ResponseEntity<PageResponse<CardResponse>> getUserCards(
            @Parameter(description = "ID of the user to retrieve cards for", required = true, example = "1")
            @PathVariable Long userId,

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
            @RequestParam(value = "maxBalance", required = false) BigDecimal maxBalance
    ) {
        PageResponse<CardResponse> pageResponse = cardsService.getUserCards(userId, page, limit, sort, cardStatus, minBalance, maxBalance);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

}
