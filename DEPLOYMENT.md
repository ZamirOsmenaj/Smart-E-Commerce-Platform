# Multi-Environment Deployment Guide

This guide explains how to deploy the Smart E-Commerce Platform across different environments using Docker Compose with environment-specific configurations.

## Overview

The application supports three deployment environments:
- **Development (dev)** - Local development with debug features
- **UAT (uat)** - User Acceptance Testing with production-like settings
- **Production (prod)** - Live environment with optimized performance and security

## Environment Files

Each environment uses its own `.env` file containing environment-specific variables:

### .env.dev (Development)
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ecommerce_dev
SPRING_DATASOURCE_USERNAME=dev_user
SPRING_DATASOURCE_PASSWORD=dev_password

# Redis Configuration
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=dev_jwt_secret_key_change_in_production
JWT_EXPIRATION=86400000

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG

# Database Migration
LIQUIBASE_CHANGELOG_PATH=db/changelog/dev/
```

### .env.uat (UAT)
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://uat-postgres:5432/ecommerce_uat
SPRING_DATASOURCE_USERNAME=uat_user
SPRING_DATASOURCE_PASSWORD=uat_secure_password

# Redis Configuration
SPRING_REDIS_HOST=uat-redis
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=uat_redis_password

# JWT Configuration
JWT_SECRET=uat_jwt_secret_key_secure
JWT_EXPIRATION=86400000

# Application Configuration
SPRING_PROFILES_ACTIVE=uat
LOG_LEVEL=INFO

# Database Migration
LIQUIBASE_CHANGELOG_PATH=db/changelog/uat/
```

### .env.prod (Production)
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-postgres:5432/ecommerce_prod
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=highly_secure_prod_password

# Redis Configuration
SPRING_REDIS_HOST=prod-redis-cluster
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=secure_redis_cluster_password
SPRING_REDIS_CLUSTER_NODES=redis-node1:6379,redis-node2:6379,redis-node3:6379

# JWT Configuration
JWT_SECRET=production_jwt_secret_key_very_secure
JWT_EXPIRATION=3600000

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
LOG_LEVEL=WARN

# Database Migration
LIQUIBASE_CHANGELOG_PATH=db/changelog/prod/
```

## Deployment Commands

### Development Environment
```bash
# Start development environment
docker compose --env-file .env.dev up --build

# Run in detached mode
docker compose --env-file .env.dev up -d --build

# View logs
docker compose --env-file .env.dev logs -f app

# Stop services
docker compose --env-file .env.dev down
```

### UAT Environment
```bash
# Start UAT environment
docker compose --env-file .env.uat up --build

# Run in detached mode
docker compose --env-file .env.uat up -d --build

# View logs
docker compose --env-file .env.uat logs -f app

# Stop services
docker compose --env-file .env.uat down
```

### Production Environment
```bash
# Start production environment
docker compose --env-file .env.prod up --build

# Run in detached mode
docker compose --env-file .env.prod up -d --build

# View logs
docker compose --env-file .env.prod logs -f app

# Stop services
docker compose --env-file .env.prod down
```

## Environment-Specific Features

### Development Environment
- **Debug Logging**: All application packages log at DEBUG level
- **Relaxed Security**: Easier testing with relaxed CORS and authentication
- **All Payment Gateways**: All payment providers enabled for testing
- **Extended Timeouts**: Longer timeouts for debugging sessions
- **Local Services**: Uses local PostgreSQL and Redis instances

### UAT Environment
- **INFO Logging**: Balanced logging for testing and troubleshooting
- **Production-like Security**: Security settings mirror production
- **Audit Logging**: All command operations are logged for audit
- **Realistic Timeouts**: Production-like timeout values
- **UAT Services**: Dedicated UAT database and Redis instances

### Production Environment
- **WARN Logging**: Minimal logging for optimal performance
- **Full Security**: All security measures enabled
- **Performance Optimized**: Caching and connection pools optimized
- **Monitoring Ready**: Configured for production monitoring
- **Cluster Support**: Redis cluster and database clustering support

## Health Checks and Monitoring

### Health Check Endpoints
```bash
# Application health
curl http://localhost:8080/actuator/health

# Detailed health (dev/uat only)
curl http://localhost:8080/actuator/health/detailed

# Application info
curl http://localhost:8080/actuator/info
```

### Service Status
```bash
# Check all services
docker compose ps

# Check specific service logs
docker compose logs app
docker compose logs postgres
docker compose logs redis
```

## Troubleshooting

### Common Issues

#### Environment File Not Found
```bash
# Error: .env file not found
# Solution: Ensure the .env file exists and is properly named
ls -la .env.*
```

#### Database Connection Issues
```bash
# Check database container
docker compose logs postgres

# Verify database is running
docker compose ps postgres

# Test database connection
docker compose exec postgres psql -U [username] -d [database]
```

#### Redis Connection Issues
```bash
# Check Redis container
docker compose logs redis

# Test Redis connection
docker compose exec redis redis-cli ping
```

### Environment Variable Validation
The application validates required environment variables at startup. If any are missing, you'll see clear error messages indicating which variables need to be set.

### Configuration Precedence
Spring Boot follows this property precedence order:
1. Environment variables
2. Profile-specific properties (application-{profile}.yml)
3. Default properties (application.yml)

## Security Considerations

### Development Environment
- Use non-production secrets
- Enable debug features for easier troubleshooting
- Relaxed security settings for testing

### UAT Environment
- Use UAT-specific secrets (not production)
- Enable audit logging
- Production-like security settings

### Production Environment
- Use strong, unique secrets
- Disable all debug features
- Enable all security measures
- Regular security audits

## Migration from Single Environment

If migrating from a single-environment setup:

1. **Backup existing configuration**
   ```bash
   cp docker-compose.yml docker-compose.yml.backup
   cp application.properties application.properties.backup
   ```

2. **Create environment files**
   - Copy your existing configuration to `.env.dev`
   - Create `.env.uat` and `.env.prod` with appropriate values

3. **Update docker-compose.yml**
   - Replace hardcoded values with environment variable placeholders
   - Ensure all services use environment variables

4. **Test each environment**
   ```bash
   # Test development
   docker compose --env-file .env.dev up --build
   
   # Test UAT
   docker compose --env-file .env.uat up --build
   
   # Test production (in staging first)
   docker compose --env-file .env.prod up --build
   ```

## Best Practices

1. **Never commit sensitive data** - Keep production secrets out of version control
2. **Use environment-specific databases** - Separate data for each environment
3. **Test configuration changes** - Always test in dev/UAT before production
4. **Monitor resource usage** - Each environment may have different resource needs
5. **Regular backups** - Especially important for UAT and production environments
6. **Document environment differences** - Keep this guide updated with any changes

## Support

For deployment issues:
1. Check the application logs: `docker compose logs app`
2. Verify environment variables are set correctly
3. Ensure all required services are running: `docker compose ps`
4. Check the troubleshooting section above
5. Review the application health endpoint: `/actuator/health`