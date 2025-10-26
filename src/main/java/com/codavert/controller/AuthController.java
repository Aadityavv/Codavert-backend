package com.codavert.controller;

import com.codavert.dto.JwtResponseDto;
import com.codavert.dto.LoginRequestDto;
import com.codavert.dto.UserRegistrationDto;
import com.codavert.entity.User;
import com.codavert.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Operation(
        summary = "User Login",
        description = "Authenticate user with username/email and password. Returns JWT token for subsequent API calls.",
        operationId = "authenticateUser"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JwtResponseDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                        "type": "Bearer",
                        "id": 1,
                        "username": "admin",
                        "email": "admin@codavert.com",
                        "roles": ["ROLE_ADMIN"]
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "error": "Invalid username or password"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - validation errors",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                    {
                        "error": "Username and password are required"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
        @Parameter(description = "Login credentials", required = true)
        @Valid @RequestBody LoginRequestDto loginRequest) {
        JwtResponseDto jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
    
    @Operation(
        summary = "User Registration",
        description = "Register a new user account with username, email, password, and roles.",
        operationId = "registerUser"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "message": "User registered successfully!"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - validation errors or user already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "error": "Username is already taken!"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
        @Parameter(description = "User registration data", required = true)
        @Valid @RequestBody UserRegistrationDto signUpRequest) {
        try {
            User user = authService.registerUser(signUpRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(
        summary = "Get User Profile",
        description = "Retrieve the current user's profile information using JWT token.",
        operationId = "getUserProfile",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "id": 1,
                        "username": "admin",
                        "email": "admin@codavert.com",
                        "roles": ["ROLE_ADMIN"],
                        "createdAt": "2024-01-01T00:00:00",
                        "updatedAt": "2024-01-01T00:00:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "error": "Unauthorized access"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(
        @Parameter(description = "JWT Authorization token", required = true)
        @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            // Properly extract username using JwtUtils
            String username = authService.getUsernameFromToken(jwt);
            User user = authService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @Operation(
        summary = "Update User Profile",
        description = "Update the current user's profile information using JWT token.",
        operationId = "updateUserProfile",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "id": 1,
                        "username": "admin",
                        "email": "admin@codavert.com",
                        "roles": ["ROLE_ADMIN"],
                        "createdAt": "2024-01-01T00:00:00",
                        "updatedAt": "2024-01-01T12:00:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - validation errors",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "error": "Email is already in use"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                        "error": "Unauthorized access"
                    }
                    """
                )
            )
        )
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
        @Parameter(description = "JWT Authorization token", required = true)
        @RequestHeader("Authorization") String token,
        @Parameter(description = "Updated user data", required = true)
        @Valid @RequestBody UserRegistrationDto updateRequest) {
        try {
            String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = authService.getUsernameFromToken(jwt);
            User user = authService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            User updatedUser = authService.updateUser(user.getId(), updateRequest);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // deprecated helper removed in favor of JwtUtils via AuthService
}
