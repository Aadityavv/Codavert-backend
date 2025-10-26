# Codavert API Documentation

## Overview

The Codavert API is a comprehensive REST API for managing freelancing projects, clients, and business operations. This documentation provides detailed information about all available endpoints, request/response formats, and authentication requirements.

## Base Information

- **Base URL**: `http://localhost:8081/api`
- **API Version**: 1.0.0
- **Authentication**: JWT Bearer Token
- **Content Type**: `application/json`

## Quick Access

- **Swagger UI**: `http://localhost:8081/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/api/api-docs`
- **H2 Database Console**: `http://localhost:8081/api/h2-console`

## Authentication

Most endpoints require JWT authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

### Getting a Token

Use the `/api/auth/signin` endpoint to obtain a JWT token:

```bash
curl -X POST http://localhost:8081/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

## API Endpoints

### Authentication Endpoints

#### POST `/api/auth/signin`
**Description**: Authenticate user and get JWT token

**Request Body**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@codavert.com",
  "roles": ["ROLE_ADMIN"]
}
```

#### POST `/api/auth/signup`
**Description**: Register a new user

**Request Body**:
```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "roles": ["ROLE_USER"]
}
```

#### GET `/api/auth/profile`
**Description**: Get current user profile (requires authentication)

**Headers**:
```
Authorization: Bearer <token>
```

#### PUT `/api/auth/profile`
**Description**: Update current user profile (requires authentication)

**Headers**:
```
Authorization: Bearer <token>
```

**Request Body**:
```json
{
  "username": "updateduser",
  "email": "updated@example.com",
  "password": "newpassword123"
}
```

### Client Management Endpoints

#### GET `/api/clients`
**Description**: Get all clients for a user (paginated)

**Parameters**:
- `userId` (required): User ID
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort field and direction

**Example**:
```
GET /api/clients?userId=1&page=0&size=10&sort=name,asc
```

**Response**:
```json
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
```

#### GET `/api/clients/{id}`
**Description**: Get client by ID

**Parameters**:
- `id` (path): Client ID

#### POST `/api/clients`
**Description**: Create a new client

**Parameters**:
- `userId` (query): User ID to associate with client

**Request Body**:
```json
{
  "name": "New Client Corp",
  "email": "contact@newclient.com",
  "phone": "+1-555-9999",
  "address": "456 New St, City, State 54321",
  "status": "ACTIVE",
  "type": "CORPORATE"
}
```

#### PUT `/api/clients/{id}`
**Description**: Update an existing client

**Parameters**:
- `id` (path): Client ID

**Request Body**: Same as POST

#### DELETE `/api/clients/{id}`
**Description**: Delete a client

**Parameters**:
- `id` (path): Client ID

#### GET `/api/clients/search`
**Description**: Search clients by name or email

**Parameters**:
- `userId` (required): User ID
- `searchTerm` (required): Search term
- `page` (optional): Page number
- `size` (optional): Page size

#### GET `/api/clients/status/{status}`
**Description**: Get clients by status

**Parameters**:
- `userId` (required): User ID
- `status` (path): Client status (ACTIVE, INACTIVE, SUSPENDED)
- `page` (optional): Page number
- `size` (optional): Page size

#### GET `/api/clients/type/{type}`
**Description**: Get clients by type

**Parameters**:
- `userId` (required): User ID
- `type` (path): Client type (CORPORATE, INDIVIDUAL, STARTUP)
- `page` (optional): Page number
- `size` (optional): Page size

#### GET `/api/clients/count`
**Description**: Get total client count for a user

**Parameters**:
- `userId` (required): User ID

#### GET `/api/clients/count/status/{status}`
**Description**: Get client count by status for a user

**Parameters**:
- `userId` (required): User ID
- `status` (path): Client status

### Project Management Endpoints

#### GET `/api/projects`
**Description**: Get all projects for a user (paginated)

**Parameters**:
- `userId` (required): User ID
- `page` (optional): Page number
- `size` (optional): Page size
- `sort` (optional): Sort field and direction

#### GET `/api/projects/{id}`
**Description**: Get project by ID

#### POST `/api/projects`
**Description**: Create a new project

**Request Body**:
```json
{
  "name": "Website Development",
  "description": "Full-stack web application development",
  "status": "IN_PROGRESS",
  "clientId": 1,
  "startDate": "2024-01-01",
  "endDate": "2024-03-31",
  "budget": 15000.00
}
```

#### PUT `/api/projects/{id}`
**Description**: Update an existing project

#### DELETE `/api/projects/{id}`
**Description**: Delete a project

#### GET `/api/projects/status/{status}`
**Description**: Get projects by status

#### GET `/api/projects/client/{clientId}`
**Description**: Get projects for a specific client

### Document Generation Endpoints

#### POST `/api/documents/generate/mou`
**Description**: Generate Memorandum of Understanding document

**Request Body**:
```json
{
  "projectId": 1,
  "clientId": 1,
  "terms": "Custom terms and conditions",
  "deliverables": ["Website", "Mobile App", "Documentation"]
}
```

#### POST `/api/documents/generate/srs`
**Description**: Generate Software Requirements Specification document

**Request Body**:
```json
{
  "projectId": 1,
  "requirements": [
    {
      "id": "REQ-001",
      "title": "User Authentication",
      "description": "Users must be able to login securely",
      "priority": "HIGH"
    }
  ]
}
```

### Time Tracking Endpoints

#### GET `/api/time-entries`
**Description**: Get time entries for a user or project

#### POST `/api/time-entries`
**Description**: Create a new time entry

**Request Body**:
```json
{
  "projectId": 1,
  "taskId": 1,
  "entryDate": "2024-01-15",
  "hoursWorked": 8.5,
  "description": "Implemented user authentication module"
}
```

#### PUT `/api/time-entries/{id}`
**Description**: Update a time entry

#### DELETE `/api/time-entries/{id}`
**Description**: Delete a time entry

### Invoice Management Endpoints

#### GET `/api/invoices`
**Description**: Get all invoices for a user

#### GET `/api/invoices/{id}`
**Description**: Get invoice by ID

#### POST `/api/invoices`
**Description**: Create a new invoice

**Request Body**:
```json
{
  "clientId": 1,
  "projectId": 1,
  "amount": 5000.00,
  "dueDate": "2024-02-15",
  "description": "Payment for Q1 development work",
  "items": [
    {
      "description": "Frontend Development",
      "quantity": 40,
      "rate": 75.00,
      "amount": 3000.00
    }
  ]
}
```

#### PUT `/api/invoices/{id}`
**Description**: Update an invoice

#### DELETE `/api/invoices/{id}`
**Description**: Delete an invoice

#### POST `/api/invoices/{id}/send`
**Description**: Send invoice to client via email

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "error": "Validation failed",
  "details": [
    {
      "field": "email",
      "message": "Email format is invalid"
    }
  ]
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized access",
  "message": "Invalid or expired token"
}
```

### 403 Forbidden
```json
{
  "error": "Access denied",
  "message": "Insufficient permissions"
}
```

### 404 Not Found
```json
{
  "error": "Resource not found",
  "message": "Client with ID 999 not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal server error",
  "message": "An unexpected error occurred"
}
```

## Data Models

### User
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@codavert.com",
  "roles": ["ROLE_ADMIN"],
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Client
```json
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
```

### Project
```json
{
  "id": 1,
  "name": "Website Development",
  "description": "Full-stack web application",
  "status": "IN_PROGRESS",
  "clientId": 1,
  "startDate": "2024-01-01",
  "endDate": "2024-03-31",
  "budget": 15000.00,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## Testing the API

### Using cURL

1. **Login**:
```bash
curl -X POST http://localhost:8081/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

2. **Get Clients** (replace TOKEN with actual token):
```bash
curl -X GET "http://localhost:8081/api/clients?userId=1" \
  -H "Authorization: Bearer TOKEN"
```

3. **Create Client**:
```bash
curl -X POST http://localhost:8081/api/clients?userId=1 \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Client",
    "email": "test@example.com",
    "phone": "+1-555-0000",
    "address": "123 Test St",
    "status": "ACTIVE",
    "type": "INDIVIDUAL"
  }'
```

### Using Swagger UI

1. Navigate to `http://localhost:8081/api/swagger-ui.html`
2. Click "Authorize" button
3. Enter your JWT token in the format: `Bearer <your-token>`
4. Test any endpoint directly from the interface

## Rate Limiting

Currently, there are no rate limits implemented. In production, consider implementing rate limiting to prevent abuse.

## Support

For API support and questions:
- **Email**: support@codavert.com
- **Documentation**: This file and Swagger UI
- **Issues**: Report bugs and feature requests through the project repository

## Changelog

### Version 1.0.0 (Current)
- Initial API release
- Authentication with JWT
- Client management
- Project management
- Document generation
- Time tracking
- Invoice management

