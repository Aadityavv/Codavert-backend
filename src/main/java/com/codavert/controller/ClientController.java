package com.codavert.controller;

import com.codavert.dto.ClientDto;
import com.codavert.entity.Client;
import com.codavert.service.ClientService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clients")
@Tag(name = "Client Management", description = "Client management endpoints for managing client information")
public class ClientController {
    
    @Autowired
    private ClientService clientService;
    
    @Operation(
        summary = "Get All Clients",
        description = "Retrieve a paginated list of all clients for a specific user.",
        operationId = "getAllClients",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Clients retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "content": [
                            {
                                "id": 1,
                                "name": "Acme Corporation",
                                "email": "contact@acme.com",
                                "phone": "+1-555-0123",
                                "address": "123 Business St, City, State 12345",
                                "status": "ACTIVE",
                                "type": "CORPORATE",
                                "createdAt": "2024-01-01T00:00:00",
                                "updatedAt": "2024-01-01T00:00:00"
                            }
                        ],
                        "pageable": {
                            "pageNumber": 0,
                            "pageSize": 10
                        },
                        "totalElements": 1,
                        "totalPages": 1
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    @GetMapping
    public ResponseEntity<Page<Client>> getAllClients(
        @Parameter(description = "User ID to filter clients", required = true)
        @RequestParam Long userId, 
        @Parameter(description = "Pagination parameters")
        Pageable pageable) {
        Page<Client> clients = clientService.getAllClientsByUserId(userId, pageable);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id)
            .map(client -> ResponseEntity.ok(client))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Create New Client",
        description = "Create a new client record for a specific user.",
        operationId = "createClient",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Client created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Client.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                    {
                        "id": 1,
                        "name": "Acme Corporation",
                        "email": "contact@acme.com",
                        "phone": "+1-555-0123",
                        "address": "123 Business St, City, State 12345",
                        "status": "ACTIVE",
                        "type": "CORPORATE",
                        "createdAt": "2024-01-01T00:00:00",
                        "updatedAt": "2024-01-01T00:00:00"
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
                        "error": "Client name is required"
                    }
                    """
                )
            )
        )
    })
    @PostMapping
    public ResponseEntity<Client> createClient(
        @Parameter(description = "Client data", required = true)
        @Valid @RequestBody ClientDto clientDto, 
        @Parameter(description = "User ID to associate with client", required = true)
        @RequestParam Long userId) {
        // Allow frontend alias mapping (name -> companyName)
        if (clientDto.getCompanyName() == null && clientDto.getName() != null) {
            clientDto.setCompanyName(clientDto.getName());
        }
        Client client = clientService.createClient(clientDto, userId);
        return ResponseEntity.ok(client);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, 
                                        @Valid @RequestBody ClientDto clientDto) {
        try {
            Client client = clientService.updateClient(id, clientDto);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.ok("Client deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Client>> searchClients(@RequestParam Long userId,
                                                     @RequestParam String searchTerm,
                                                     Pageable pageable) {
        Page<Client> clients = clientService.searchClients(userId, searchTerm, pageable);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Client>> getClientsByStatus(@RequestParam Long userId,
                                                          @PathVariable String status,
                                                          Pageable pageable) {
        Page<Client> clients = clientService.getClientsByStatus(userId, status, pageable);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<Client>> getClientsByType(@RequestParam Long userId,
                                                        @PathVariable String type,
                                                        Pageable pageable) {
        Page<Client> clients = clientService.getClientsByType(userId, type, pageable);
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getClientCount(@RequestParam Long userId) {
        Long count = clientService.getClientCountByUserId(userId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getClientCountByStatus(@RequestParam Long userId,
                                                      @PathVariable String status) {
        Long count = clientService.getClientCountByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(count);
    }
}
