# Documentation Index

Welcome to the Smart E-Commerce Platform documentation! This index provides an overview of all available documentation and helps you find what you're looking for.

## 📋 Documentation Overview

| Document | Description | Audience | Estimated Reading Time |
|----------|-------------|----------|----------------------|
| [Main README](../README.md) | Project overview and quick start | Everyone | 5 minutes |
| [Architecture Guide](ARCHITECTURE.md) | Design patterns and system architecture | Developers, Architects | 15 minutes |
| [Technology Stack](TECHNOLOGY_STACK.md) | Technologies and configurations | Developers, DevOps | 10 minutes |
| [API Reference](API_REFERENCE.md) | Complete API documentation | Developers, Integrators | 20 minutes |
| [Testing Guide](TESTING_GUIDE.md) | Setup and testing instructions | Developers, QA | 15 minutes |
| [Development Guide](DEVELOPMENT_GUIDE.md) | Development workflow and best practices | Developers | 25 minutes |
| [Commands Reference](../COMMANDS.md) | Ready-to-use API commands | Developers, Testers | 10 minutes |

## 🎯 Quick Start Paths

### For New Developers
1. Start with [Main README](../README.md) for project overview
2. Read [Architecture Guide](ARCHITECTURE.md) to understand design patterns
3. Follow [Development Guide](DEVELOPMENT_GUIDE.md) for setup
4. Use [Commands Reference](../COMMANDS.md) for testing

### For API Integration
1. Review [API Reference](API_REFERENCE.md) for endpoint details
2. Use [Commands Reference](../COMMANDS.md) for testing
3. Check [Testing Guide](TESTING_GUIDE.md) for setup instructions

### For System Understanding
1. Read [Architecture Guide](ARCHITECTURE.md) for design patterns
2. Review [Technology Stack](TECHNOLOGY_STACK.md) for technical details

## 🏗️ Architecture Documentation

### Design Patterns Covered
- **Creational (1)**: Factory Pattern
- **Structural (3)**: Adapter, Proxy, Facade Patterns
- **Behavioral (7)**: Chain of Responsibility, Observer, Strategy, Template Method, State, Command, Decorator Patterns

### Key Architectural Concepts
- **Layered Architecture**: Clear separation of concerns
- **Event-Driven Design**: Observer pattern for notifications
- **Caching Strategy**: Proxy pattern with Redis
- **Payment Processing**: Strategy and Adapter patterns
- **Order Management**: State and Command patterns

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

## 🔧 API Documentation

### REST Endpoints
- **Authentication**: `/api/auth/*` - Registration, login
- **Products**: `/api/products/*` - Product management
- **Orders**: `/api/orders/*` - Order lifecycle management
- **Payments**: `/api/payments/*` - Payment processing

### SOAP Services
- **Order Management** - Legacy system integration
- **WSDL Location**: `http://localhost:8080/ws/orders.wsdl`

## 🧪 Testing Resources

### Available Test Scenarios
- **User Registration and Authentication**
- **Product Management (Proxy Pattern Testing)**
- **Order Creation and Management (Multiple Patterns)**
- **Payment Processing (Strategy and Adapter Patterns)**

### Testing Tools
- **cURL Commands** - Ready-to-use in [Commands Reference](../COMMANDS.md)
- **Docker Setup** - Complete environment in [Testing Guide](TESTING_GUIDE.md)
- **Validation Scripts** - Documentation validation tools

## 📊 Project Metrics

### Code Organization
- **11 Design Patterns** implemented across all categories
- **26+ Packages** organized by pattern and functionality
- **4 API Controllers** with comprehensive endpoints
- **7 Observer Implementations** for event handling

### Documentation Coverage
- **8 Documentation Files** covering all aspects
- **100+ API Examples** with request/response samples
- **50+ cURL Commands** ready for testing
- **Cross-referenced Navigation** between all documents

## 🔍 Search and Navigation

### Finding Information
- **By Pattern**: Use [Architecture Guide](ARCHITECTURE.md) pattern index
- **By Technology**: Check [Technology Stack](TECHNOLOGY_STACK.md) sections
- **By API**: Browse [API Reference](API_REFERENCE.md) endpoints
- **By Task**: Follow [Development Guide](DEVELOPMENT_GUIDE.md) workflows

### Navigation Features
- **Table of Contents** in each document
- **Cross-references** between related sections
- **Quick Links** for common tasks
- **Back to Top** links in longer documents

## 🚀 Getting Started Checklist

### For Development
- [ ] Read [Main README](../README.md)
- [ ] Set up development environment using [Development Guide](DEVELOPMENT_GUIDE.md)
- [ ] Run validation script: `./scripts/validate-docs.py`
- [ ] Test API using [Commands Reference](../COMMANDS.md)

### For Integration
- [ ] Review [API Reference](API_REFERENCE.md)
- [ ] Set up test environment using [Testing Guide](TESTING_GUIDE.md)
- [ ] Test authentication flow
- [ ] Implement error handling patterns

### For Architecture Review
- [ ] Study [Architecture Guide](ARCHITECTURE.md)
- [ ] Understand design pattern implementations
- [ ] Review [Technology Stack](TECHNOLOGY_STACK.md)
- [ ] Examine code organization principles

## 🔗 Quick Navigation

| Section | Link |
|---------|------|
| 🏠 Home | [Main README](../README.md) |
| 🏗️ Architecture | [Architecture Guide](ARCHITECTURE.md) |
| 🛠️ Technology | [Technology Stack](TECHNOLOGY_STACK.md) |
| 🔧 API | [API Reference](API_REFERENCE.md) |
| 🧪 Testing | [Testing Guide](TESTING_GUIDE.md) |
| 👨‍💻 Development | [Development Guide](DEVELOPMENT_GUIDE.md) |
| 📋 Commands | [Commands Reference](../COMMANDS.md) |

**Last Updated**: Generated automatically with comprehensive documentation suite

[⬆️ Back to Top](#documentation-index)