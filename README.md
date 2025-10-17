# Smart E-Commerce Platform

A sophisticated Spring Boot e-commerce application showcasing **11 design patterns** across all three categories, modern technologies, and enterprise-grade architecture.

## Table of Contents

- [Architecture Overview](#️-architecture-overview)
- [Quick Start](#-quick-start)
- [Technology Stack](#️-technology-stack)
- [Documentation](#-documentation)
- [API Overview](#-api-overview)
- [Development Workflow](#️‍♂️-development-workflow)
- [Key Features](#-key-features)
- [Package Structure](#-package-structure)
- [Testing](#-testing)
- [Future Enhancements](#-future-enhancements)
- [Project Status](#-project-status)

## 🏗️ Architecture Overview

This project demonstrates a comprehensive implementation of design patterns in a real-world e-commerce context, featuring advanced caching, payment processing, order management, and notification systems.

### Design Patterns Implemented

| Category | Pattern | Implementation | Purpose |
|----------|---------|----------------|---------|
| **Creational (1)** | Factory | Entity creation (`factory/`) | Standardized object creation |
| **Structural (3)** | Adapter | Payment providers (`adapter/`) | Third-party payment integration |
| | Proxy | Product caching (`proxy/`) | Performance optimization |
| | Facade | Response mapping (`mapper/`) | Simplified API responses |
| **Behavioral (7)** | Chain of Responsibility | Order validation (`validation/`) | Flexible validation pipeline |
| | Observer | Order status changes (`observer/`) | Event-driven notifications |
| | Strategy | Payment processing (`payment/`) | Multiple payment methods |
| | Template Method | Payment flow (`payment/`) | Consistent payment workflow |
| | State | Order lifecycle (`state/`) | Order status management |
| | Command | Order operations (`command/`) | Undoable operations |
| | Decorator | Notifications (`decorator/`) | Flexible notification channels |

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 17+ (for local development)

### Multi-Environment Deployment

This application supports multiple deployment environments using Docker Compose with environment-specific configuration files:

#### Development Environment
```bash
# Clone the repository
git clone <repository-url>
cd smart-ecommerce-platform

# Deploy to development environment
docker compose --env-file .env.dev up --build

# Or run in detached mode
docker compose --env-file .env.dev up -d --build
```

#### UAT Environment
```bash
# Deploy to UAT environment
docker compose --env-file .env.uat up --build
```

#### Production Environment
```bash
# Deploy to production environment
docker compose --env-file .env.prod up --build
```

### Environment Configuration Files

Each environment uses its own configuration file:
- **`.env.dev`** - Development environment variables
- **`.env.uat`** - UAT environment variables  
- **`.env.prod`** - Production environment variables

### Services
- **Application**: `http://localhost:8080`
- **PostgreSQL**: `localhost:5432`
- **Redis**: `localhost:6379`

### Verify Deployment
```bash
# Check running services
docker compose ps

# View application logs
docker compose logs app

# Health check
curl http://localhost:8080/actuator/health
```

For detailed deployment instructions, environment configuration, and troubleshooting, see the [Deployment Guide](DEPLOYMENT.md).

## 🛠️ Technology Stack

### Core Technologies
- **Spring Boot 3.x** - Application framework
- **PostgreSQL** - Primary database
- **Redis** - Caching and session storage
- **Docker** - Containerization

### Key Features
- **JWT Authentication** - Secure token-based auth
- **Liquibase** - Database migration management
- **Quartz Scheduler** - Background job processing
- **SOAP Web Services** - Legacy system integration
- **Lombok** - Code generation
- **Generics** - Type-safe collections

### Design Pattern Integration
- **Proxy Pattern** with Redis caching
- **Observer Pattern** for real-time notifications
- **Command Pattern** with audit trail
- **Strategy Pattern** for payment providers
- **State Pattern** for order lifecycle

## 📚 Documentation

### Detailed Guides
- **[Documentation Index](docs/INDEX.md)** - Complete documentation overview and navigation
- **[Architecture Guide](docs/ARCHITECTURE.md)** - Deep dive into design patterns and system architecture
- **[Technology Stack](docs/TECHNOLOGY_STACK.md)** - Comprehensive technology documentation
- **[API Reference](docs/API_REFERENCE.md)** - Complete REST and SOAP API documentation
- **[Testing Guide](docs/TESTING_GUIDE.md)** - Setup and testing instructions
- **[Development Guide](docs/DEVELOPMENT_GUIDE.md)** - Development workflow and best practices
- **[Commands Reference](COMMANDS.md)** - Ready-to-use API commands

## 🌍 Environment Setup

### Environment Variables Configuration

Each environment requires specific configuration through `.env` files:

#### Development (.env.dev)
- Local database and Redis instances
- Debug logging enabled
- Relaxed security for testing
- All payment gateways enabled

#### UAT (.env.uat)
- UAT database and Redis instances
- INFO level logging
- Production-like security settings
- Audit logging enabled

#### Production (.env.prod)
- Production database and Redis cluster
- WARN level logging for performance
- Full security measures enabled
- Optimized caching and performance settings

### Required Environment Variables

All environments require these variables in their respective `.env` files:
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ecommerce_dev
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# Redis Configuration
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000

# Application Configuration
SPRING_PROFILES_ACTIVE=dev
```

## 🔧 API Overview

### REST Endpoints
- **Authentication**: `/api/auth/*` - Registration, login
- **Products**: `/api/products/*` - Product management
- **Orders**: `/api/orders/*` - Order lifecycle management
- **Payments**: `/api/payments/*` - Payment processing

### SOAP Services
- **Order Management** - Legacy system integration
- **Order Status Updates** - Real-time status synchronization

## 🏃‍♂️ Development Workflow

### Key Configuration
- **Environment Variables**: See `.env.dev`, `.env.uat`, `.env.prod` files
- **Application Properties**: `src/main/resources/application-{profile}.yml`
- **Database Migrations**: `src/main/resources/db/changelog/{environment}/`
- **Docker Compose**: Uses environment variables from `.env` files

## 🎯 Key Features

### Advanced Order Management
- **State Pattern** - Robust order lifecycle management
- **Command Pattern** - Undoable operations with audit trail
- **Chain of Responsibility** - Flexible validation pipeline
- **Observer Pattern** - Real-time status notifications

### Payment Processing
- **Strategy Pattern** - Multiple payment providers (Stripe, PayPal)
- **Adapter Pattern** - Unified payment interface
- **Template Method** - Consistent payment workflow

### Performance & Caching
- **Proxy Pattern** - Intelligent product caching with Redis
- **Configurable Strategies** - Granular vs. bulk caching

### Notification System
- **Decorator Pattern** - Flexible notification channels
- **Observer Pattern** - Event-driven notifications
- **Factory Pattern** - Dynamic notification service creation

## 🔍 Package Structure

```
com.example.ecommerce/
├── adapter/          # Adapter pattern implementations
├── command/          # Command pattern for operations
├── config/           # Spring configuration classes
├── controller/       # REST controllers
├── decorator/        # Decorator pattern for notifications
├── domain/           # Core business entities
├── factory/          # Factory pattern for entity creation
├── observer/         # Observer pattern implementations
├── payment/          # Strategy & Template Method patterns
├── proxy/            # Proxy pattern for caching
├── state/            # State pattern for order management
├── validation/       # Chain of Responsibility pattern
└── ...
```

## 🧪 Testing

Quick test commands are available in [COMMANDS.md](COMMANDS.md). For comprehensive testing instructions, see the [Testing Guide](docs/TESTING_GUIDE.md).

### Quick Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 🚧 Future Enhancements

### Planned Features
- **JMS Integration** - Message queue for microservices
- **Quarkus Migration** - Cloud-native runtime
- **OpenShift Deployment** - Enterprise container platform
- **Decision Tables** - Business rule management
- **Microservices Architecture** - Service decomposition

## 📈 Project Status

### Completed ✅
- All 11 design patterns implemented
- Complete REST API with authentication
- SOAP web services integration
- Docker containerization
- Redis caching system
- Centralized message management
- Comprehensive error handling
- Multi-environment deployment profiles

### In Progress 🚧
- Unit and integration tests
- Performance optimization

## 📄 License

This project is for educational and demonstration purposes.

---

**Built with ❤️ using Spring Boot and 11 Design Patterns**

---

## 🔗 Quick Navigation

| Documentation | Description |
|---------------|-------------|
| [Documentation Index](docs/INDEX.md) | Complete documentation overview and navigation |
| [Architecture Guide](docs/ARCHITECTURE.md) | Design patterns and system architecture |
| [Technology Stack](docs/TECHNOLOGY_STACK.md) | Technologies, configurations, and integrations |
| [API Reference](docs/API_REFERENCE.md) | Complete REST and SOAP API documentation |
| [Testing Guide](docs/TESTING_GUIDE.md) | Setup, testing, and interaction guide |
| [Development Guide](docs/DEVELOPMENT_GUIDE.md) | Development workflow and best practices |
| [Commands Reference](COMMANDS.md) | Ready-to-use cURL commands |
| [Deployment Guide](DEPLOYMENT.md) | Multi-environment deployment instructions |
