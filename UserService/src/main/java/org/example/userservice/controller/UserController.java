package org.example.userservice.controller;

import org.example.userservice.dto.UserRequestDTO;
import org.example.userservice.dto.UserResponseDTO;
import org.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID of the user to be retrieved") @PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all users with optional filtering")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by surname") @RequestParam(required = false) String surname) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<UserResponseDTO> users = userService.getAllUsers(pageable, name, surname);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to be updated") @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Activate user", description = "Activates a user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User activated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Map<String, String>> activateUser(
            @Parameter(description = "ID of the user to be activated") @PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.ok(Map.of("message", "User activated successfully"));
    }

    @Operation(summary = "Deactivate user", description = "Deactivates a user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(
            @Parameter(description = "ID of the user to be deactivated") @PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }

    @Operation(summary = "Delete user", description = "Deletes a user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @Parameter(description = "ID of the user to be deleted") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }
}