# Docker Deployment Guide

## Prerequisites
- Docker installed (version 20.10 or higher)
- Docker Compose installed (version 2.0 or higher)

## Quick Start

### 1. Using Docker Compose (Recommended)

The easiest way to run the entire application stack:

```bash
# Start all services (database + backend)
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop all services
docker-compose down

# Stop and remove volumes (clean start)
docker-compose down -v
```

The application will be available at:
- Backend API: http://localhost:8080
- API Documentation (Swagger): http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

### 2. Using Docker Only

If you want to run just the backend container:

```bash
# Build the image
docker build -t codavert-backend:latest .

# Run the container (requires external PostgreSQL)
docker run -d \
  --name codavert-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/codavert_db \
  -e SPRING_DATASOURCE_USERNAME=codavert_user \
  -e SPRING_DATASOURCE_PASSWORD=codavert_password \
  -e JWT_SECRET=your-very-secure-secret-key \
  codavert-backend:latest

# View logs
docker logs -f codavert-backend

# Stop container
docker stop codavert-backend

# Remove container
docker rm codavert-backend
```

## Configuration

### Environment Variables

The following environment variables can be configured:

#### Database
- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password

#### Security
- `JWT_SECRET` - JWT signing secret (min 512 bits)
- `JWT_EXPIRATION` - JWT token expiration time in milliseconds

#### Mail (Optional)
- `SPRING_MAIL_HOST` - SMTP server host
- `SPRING_MAIL_PORT` - SMTP server port
- `SPRING_MAIL_USERNAME` - Email username
- `SPRING_MAIL_PASSWORD` - Email password

#### CORS
- `CORS_ALLOWED_ORIGINS` - Comma-separated list of allowed origins

### Production Configuration

For production deployment, update `docker-compose.yml`:

1. **Change default passwords**:
   ```yaml
   POSTGRES_PASSWORD: use-strong-password-here
   JWT_SECRET: use-very-strong-secret-min-512-bits
   ```

2. **Configure mail settings** with your SMTP provider

3. **Update CORS origins** with your frontend URLs:
   ```yaml
   CORS_ALLOWED_ORIGINS: https://yourdomain.com,https://www.yourdomain.com
   ```

4. **Enable SSL/TLS** (recommended for production)

## Docker Commands

### Build Commands
```bash
# Build the image
docker build -t codavert-backend:latest .

# Build with specific tag
docker build -t codavert-backend:1.0.0 .

# Build without cache
docker build --no-cache -t codavert-backend:latest .
```

### Container Management
```bash
# Start container
docker start codavert-backend

# Stop container
docker stop codavert-backend

# Restart container
docker restart codavert-backend

# View container logs
docker logs codavert-backend

# Follow logs in real-time
docker logs -f codavert-backend

# Execute command in container
docker exec -it codavert-backend sh

# Inspect container
docker inspect codavert-backend
```

### Docker Compose Commands
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs

# Follow logs
docker-compose logs -f

# View specific service logs
docker-compose logs backend

# Restart specific service
docker-compose restart backend

# Rebuild and restart
docker-compose up -d --build

# Scale service (if needed)
docker-compose up -d --scale backend=3

# Remove volumes
docker-compose down -v
```

## Health Checks

The backend includes health check endpoints:

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check database connectivity
curl http://localhost:8080/actuator/health/db

# View metrics
curl http://localhost:8080/actuator/metrics
```

## Troubleshooting

### Backend won't start
1. Check if PostgreSQL is running:
   ```bash
   docker-compose ps postgres
   ```

2. Check backend logs:
   ```bash
   docker-compose logs backend
   ```

3. Verify database connection:
   ```bash
   docker exec -it codavert-postgres psql -U codavert_user -d codavert_db
   ```

### Database connection issues
1. Ensure PostgreSQL is healthy:
   ```bash
   docker-compose ps
   ```

2. Check network connectivity:
   ```bash
   docker network inspect codavert-network
   ```

### Memory issues
If experiencing memory problems, adjust Java memory settings in `docker-compose.yml`:
```yaml
environment:
  JAVA_OPTS: "-Xmx1g -Xms512m"
```

## Production Deployment

For production deployment on cloud platforms:

### AWS ECS/Fargate
1. Push image to ECR
2. Create ECS task definition
3. Configure RDS PostgreSQL
4. Set environment variables in task definition

### Google Cloud Run
```bash
# Build and push to GCR
gcloud builds submit --tag gcr.io/PROJECT_ID/codavert-backend

# Deploy to Cloud Run
gcloud run deploy codavert-backend \
  --image gcr.io/PROJECT_ID/codavert-backend \
  --platform managed \
  --region us-central1 \
  --set-env-vars "SPRING_DATASOURCE_URL=..."
```

### Docker Swarm
```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml codavert

# List services
docker service ls

# Scale service
docker service scale codavert_backend=3
```

### Kubernetes
See `k8s/` directory for Kubernetes manifests (if available).

## Monitoring

### View Resource Usage
```bash
# Container stats
docker stats codavert-backend

# Docker Compose stats
docker-compose stats
```

### Export Logs
```bash
# Export logs to file
docker-compose logs backend > backend-logs.txt

# Export with timestamp
docker-compose logs --timestamps backend > backend-logs-$(date +%Y%m%d).txt
```

## Security Best Practices

1. **Never commit secrets** to version control
2. **Use Docker secrets** for sensitive data in production
3. **Run containers as non-root user** (already configured)
4. **Keep base images updated** regularly
5. **Scan images for vulnerabilities**:
   ```bash
   docker scan codavert-backend:latest
   ```
6. **Use specific image tags** instead of `latest` in production
7. **Limit container resources**:
   ```yaml
   deploy:
     resources:
       limits:
         cpus: '1.0'
         memory: 1G
   ```

## Backup and Restore

### Backup Database
```bash
# Backup PostgreSQL
docker exec codavert-postgres pg_dump -U codavert_user codavert_db > backup.sql

# Or using docker-compose
docker-compose exec postgres pg_dump -U codavert_user codavert_db > backup-$(date +%Y%m%d).sql
```

### Restore Database
```bash
# Restore PostgreSQL
docker exec -i codavert-postgres psql -U codavert_user -d codavert_db < backup.sql
```

## Support

For issues or questions:
- Check logs: `docker-compose logs backend`
- Review API documentation: http://localhost:8080/swagger-ui.html
- Create an issue on the repository

