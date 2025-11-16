# Codavert Backend API

A comprehensive Spring Boot REST API for managing freelancing projects, clients, and business operations.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone and navigate to the backend directory**:
   ```bash
   cd codavert-backend
   ```

2. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Access the API**:
   - **Base URL**: `http://localhost:8081/api`
   - **Swagger UI**: `http://localhost:8081/api/swagger-ui.html`
   - **API Docs**: `http://localhost:8081/api/api-docs`
   - **H2 Console**: `http://localhost:8081/api/h2-console`

## 📚 API Documentation

### Interactive Documentation
- **Swagger UI**: `http://localhost:8081/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/api/api-docs`

### Static Documentation
- **Complete API Guide**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

## 🔐 Authentication

### Default Credentials
- **Username**: `admin`
- **Password**: `admin123`

### Getting a Token
```bash
curl -X POST http://localhost:8081/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### Using the Token
Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## 🗄️ Database

The application uses H2 in-memory database for development:
- **JDBC URL**: `jdbc:h2:mem:codavert_db`
- **Username**: `sa`
- **Password**: (empty)
- **Console**: `http://localhost:8081/api/h2-console`

## 📋 Available Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration
- `GET /api/auth/profile` - Get user profile
- `PUT /api/auth/profile` - Update user profile

### Client Management
- `GET /api/clients` - Get all clients (paginated)
- `GET /api/clients/{id}` - Get client by ID
- `POST /api/clients` - Create new client
- `PUT /api/clients/{id}` - Update client
- `DELETE /api/clients/{id}` - Delete client
- `GET /api/clients/search` - Search clients
- `GET /api/clients/status/{status}` - Get clients by status
- `GET /api/clients/type/{type}` - Get clients by type
- `GET /api/clients/count` - Get client count

### Project Management
- `GET /api/projects` - Get all projects (paginated)
- `GET /api/projects/{id}` - Get project by ID
- `POST /api/projects` - Create new project
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `GET /api/projects/status/{status}` - Get projects by status
- `GET /api/projects/client/{clientId}` - Get projects for client

### Document Generation
- `POST /api/documents/generate/mou` - Generate MOU document
- `POST /api/documents/generate/srs` - Generate SRS document

### Time Tracking
- `GET /api/time-entries` - Get time entries
- `POST /api/time-entries` - Create time entry
- `PUT /api/time-entries/{id}` - Update time entry
- `DELETE /api/time-entries/{id}` - Delete time entry

### Invoice Management
- `GET /api/invoices` - Get all invoices
- `GET /api/invoices/{id}` - Get invoice by ID
- `POST /api/invoices` - Create new invoice
- `PUT /api/invoices/{id}` - Update invoice
- `DELETE /api/invoices/{id}` - Delete invoice
- `POST /api/invoices/{id}/send` - Send invoice via email

## 🛠️ Development

### Project Structure
```
src/
├── main/
│   ├── java/com/codavert/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/      # JPA repositories
│   │   ├── security/       # Security configuration
│   │   └── service/        # Business logic
│   └── resources/
│       └── application.properties
└── test/                   # Test files
```

### Key Technologies
- **Spring Boot 3.2.0**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database operations
- **H2 Database** - In-memory database
- **JWT** - Token-based authentication
- **Swagger/OpenAPI** - API documentation
- **Maven** - Dependency management

## 🔧 Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:h2:mem:codavert_db
spring.datasource.username=sa
spring.datasource.password=

# JWT Configuration
jwt.secret=codavert-super-secret-key-for-jwt-token-generation
jwt.expiration=86400000

# Swagger Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## 🧪 Testing

### Using Swagger UI
1. Navigate to `http://localhost:8081/api/swagger-ui.html`
2. Click "Authorize" and enter your JWT token
3. Test endpoints directly from the interface

### Using cURL
```bash
# Login
curl -X POST http://localhost:8081/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'

# Get clients (replace TOKEN with actual token)
curl -X GET "http://localhost:8081/api/clients?userId=1" \
  -H "Authorization: Bearer TOKEN"
```

## 📝 Notes

- The application uses H2 in-memory database for development
- Data is reset on each application restart
- JWT tokens expire after 24 hours by default
- All endpoints require authentication except `/api/auth/signin` and `/api/auth/signup`

## 🆘 Troubleshooting

### Common Issues

1. **Port already in use**: Change `server.port` in `application.properties`
2. **Database connection error**: Ensure H2 dependency is included
3. **Authentication failed**: Check username/password or token validity

### Logs
Enable debug logging by setting:
```properties
logging.level.com.codavert=DEBUG
```

## 📞 Support

For questions or issues:
- Check the [API Documentation](./API_DOCUMENTATION.md)
- Use Swagger UI for interactive testing
- Review application logs for error details