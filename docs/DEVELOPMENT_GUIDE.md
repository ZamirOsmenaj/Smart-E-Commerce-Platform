# Development Guide

This guide provides comprehensive information for developers working on the Smart E-Commerce Platform, including setup, conventions, and best practices.

## Table of Contents

- [Development Environment Setup](#development-environment-setup)
- [Code Organization](#code-organization)
- [Design Pattern Guidelines](#design-pattern-guidelines)
- [Adding New Features](#adding-new-features)
- [Testing Strategies](#testing-strategies)
- [Best Practices](#best-practices)

## Development Environment Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- IDE (IntelliJ IDEA recommended)
- Git

### Local Development Setup
```bash
# Clone the repository
git clone <repository-url>
cd smart-ecommerce-platform

# Install dependencies
./mvnw clean install

# Start infrastructure services only
docker-compose up -d postgres redis

# Run application locally
./mvnw spring-boot:run

# Or run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### IDE Configuration

#### IntelliJ IDEA Setup
1. **Import Project**: File → Open → Select `pom.xml`
2. **Enable Lombok**: Install Lombok plugin and enable annotation processing
3. **Code Style**: Import code style from `.editorconfig`
4. **Run Configuration**: Create Spring Boot run configuration

#### VS Code Setup
1. **Extensions**: Install Java Extension Pack, Spring Boot Extension Pack
2. **Settings**: Configure Java path and Maven settings
3. **Lombok**: Install Lombok Annotations Support

### Environment Variables for Development
```bash
# Create .env.dev file for local development
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ecommerce
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
JWT_SECRET=mysuperlongsecretkeyforjwt1234567890
JWT_EXPIRATION=3600000
```

## Code Organization

### Package Structure Philosophy
The codebase is organized by design patterns and functional areas:
```

com.example.ecommerce/
├── adapter/              # Adapter Pattern implementations
├── command/              # Command Pattern - Undoable operations
│   └── order/           # Order-specific commands
├── config/              # Spring configuration classes
├── constants/           # Application constants and messages
├── controller/          # REST controllers and SOAP endpoints
├── decorator/           # Decorator Pattern - Notification decorators
├── domain/              # Core business entities (JPA entities)
├── dto/                 # Data Transfer Objects
│   ├── request/         # Request DTOs
│   └── response/        # Response DTOs
├── enums/               # Enumeration classes
├── event/               # Event handling support
├── external/            # External service integrations
├── factory/             # Factory Pattern - Entity creation
├── jobs/                # Scheduled jobs (Quartz)
├── mapper/              # Facade Pattern - Entity to DTO mapping
├── observer/            # Observer Pattern - Event observers
├── payment/             # Strategy & Template Method - Payment processing
├── proxy/               # Proxy Pattern - Caching implementations
├── repository/          # JPA repositories
├── security/            # Security and ownership validation
├── service/             # Business logic services
├── soap/                # SOAP web service classes
├── state/               # State Pattern - Order state management
├── util/                # Utility classes
└── validation/          # Chain of Responsibility - Validation handlers
```

### Naming Conventions

#### Classes
- **Entities**: `Order`, `Product`, `User` (singular nouns)
- **DTOs**: `CreateOrderRequestDTO`, `OrderResponseDTO` (descriptive + DTO suffix)
- **Services**: `OrderService`, `PaymentService` (noun + Service)
- **Controllers**: `OrderController`, `ProductController` (noun + Controller)
- **Repositories**: `OrderRepository`, `ProductRepository` (noun + Repository)

#### Methods
- **Service Methods**: `createOrder()`, `findById()`, `processPayment()`
- **Repository Methods**: `findByUserId()`, `existsByEmail()`
- **Controller Methods**: `getOrders()`, `createOrder()`, `processPayment()`

#### Constants
```java
// Message constants
public static final String ORDER_CREATED_SUCCESS = "Order created successfully";
public static final String ORDER_CREATION_FAILED_CODE = "ORDER_CREATION_FAILED";

// Configuration constants
public static final String AUTH_HEADER = "Authorization";
public static final Duration CACHE_TTL = Duration.ofMinutes(30);
```

## Design Pattern Guidelines

### When to Use Each Pattern

#### Factory Pattern
**Use When**: Creating complex objects with multiple initialization steps
```java
// Good: Centralized creation logic
Order order = OrderFactory.createNewOrder(userId, total, items);

// Avoid: Direct constructor calls with complex logic
Order order = new Order();
order.setUserId(userId);
order.setStatus(OrderStatus.PENDING);
order.setCreatedAt(Instant.now());
// ... more initialization
```

#### Adapter Pattern
**Use When**: Integrating with external APIs that have different interfaces
```java
// Good: Unified interface for different payment providers
@Component("newPaymentProvider")
public class NewPaymentAdapter extends AbstractPaymentProcessor {
    @Override
    protected PaymentResponseDTO doProcessPayment(Order order) {
        // Adapt external API to our interface
    }
}
```

#### Proxy Pattern
**Use When**: Adding cross-cutting concerns (caching, logging, security)
```java
// Good: Transparent caching without changing business logic
@Component
public class ServiceCachingProxy implements ServiceContract {
    // Caching logic here
}
```

#### Chain of Responsibility
**Use When**: Multiple validation or processing steps that can vary
```java
// Good: Flexible validation pipeline
public class NewValidationHandler extends OrderValidationHandler {
    @Override
    protected ValidationResult doValidate(CreateOrderRequestDTO request) {
        // Specific validation logic
    }
}
```

#### Observer Pattern
**Use When**: Multiple components need to react to events
```java
// Good: Decoupled event handling
@Component
public class NewOrderObserver implements OrderStatusObserver {
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        // React to status change
    }
}
```

### Pattern Implementation Guidelines

#### Strategy Pattern Implementation
```java
// 1. Define the strategy interface
public interface PaymentStrategy {
    PaymentResponseDTO processPayment(Order order);
}

// 2. Implement concrete strategies
@Component("newPaymentStrategy")
public class NewPaymentStrategy implements PaymentStrategy {
    @Override
    public PaymentResponseDTO processPayment(Order order) {
        // Implementation
    }
}

// 3. Use strategy in service
@Service
public class PaymentService {
    private final Map<String, PaymentStrategy> strategies;
    
    public PaymentResponseDTO pay(UUID orderId, String provider) {
        PaymentStrategy strategy = strategies.get(provider);
        return strategy.processPayment(order);
    }
}
```

#### Command Pattern Implementation
```java
// 1. Implement Command interface
public class NewOrderCommand implements Command {
    @Override
    public CommandResult execute() throws Exception {
        // Command execution logic
    }
    
    @Override
    public CommandResult undo() throws Exception {
        // Undo logic (if supported)
    }
    
    @Override
    public boolean supportsUndo() {
        return true; // or false
    }
}

// 2. Register with CommandFactory
@Component
public class CommandFactory {
    public Command createCommand(String type, Object... params) {
        return switch (type) {
            case "newOrder" -> new NewOrderCommand(params);
            // ... other commands
        };
    }
}
```

## Adding New Features

### Step-by-Step Feature Addition

#### 1. Domain Model
```java
// Add new entity in domain package
@Entity
@Table(name = "new_entities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    // Other fields
}
```

#### 2. Repository Layer
```java
// Add repository interface
@Repository
public interface NewEntityRepository extends JpaRepository<NewEntity, UUID> {
    List<NewEntity> findByUserId(UUID userId);
    // Custom query methods
}
```

#### 3. Service Layer
```java
// Add service with business logic
@Service
@RequiredArgsConstructor
@Slf4j
public class NewEntityService {
    private final NewEntityRepository repository;
    
    public NewEntity create(CreateNewEntityRequestDTO request) {
        // Use Factory pattern for creation
        NewEntity entity = NewEntityFactory.create(request);
        return repository.save(entity);
    }
}
```

#### 4. Controller Layer
```java
// Add REST controller
@RestController
@RequestMapping("/api/new-entities")
@RequiredArgsConstructor
@Slf4j
public class NewEntityController {
    private final NewEntityService service;
    
    @PostMapping
    public ResponseEntity<ApiResponse<NewEntityResponseDTO>> create(
            @RequestBody CreateNewEntityRequestDTO request) {
        try {
            NewEntity entity = service.create(request);
            NewEntityResponseDTO response = MapperFacade.toResponseDTO(entity);
            return ResponseUtil.success(response, MessageConstants.NEW_ENTITY_CREATED_SUCCESS);
        } catch (Exception e) {
            return ResponseUtil.error(e.getMessage(), MessageConstants.NEW_ENTITY_CREATION_FAILED_CODE);
        }
    }
}
```

#### 5. DTOs and Mapping
```java
// Request DTO
@Data
public class CreateNewEntityRequestDTO {
    private String name;
    private String description;
}

// Response DTO
@Data
@Builder
public class NewEntityResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
}

// Add mapping to MapperFacade
public static NewEntityResponseDTO toResponseDTO(NewEntity entity) {
    return NewEntityResponseDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .build();
}
```

#### 6. Database Migration
```yaml
# Create Liquibase changeset
databaseChangeLog:
  - changeSet:
      id: create-new-entities-table
      author: developer
      changes:
        - createTable:
            tableName: new_entities
            columns:
              - column:
                  name: id
                  type: UUID
                  constraints:
                    primaryKey: true
              - column:
                  name: name
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              - column:
                  name: description
                  type: TEXT
              - column:
                  name: created_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
```

### Following Existing Patterns

#### Adding New Payment Provider (Adapter Pattern)
```java
@Component("newPaymentProvider")
@ConditionalOnProperty(name = "app.payment.newprovider.enabled", havingValue = "true")
public class NewPaymentAdapter extends AbstractPaymentProcessor {
    
    private final NewPaymentGateway gateway;
    
    @Override
    protected PaymentResponseDTO doProcessPayment(Order order) {
        // Adapt new provider's API to our interface
        NewPaymentResult result = gateway.processPayment(
            convertOrderToNewFormat(order)
        );
        return convertResultToResponse(result);
    }
    
    @Override
    protected String getProviderName() {
        return "NewProvider";
    }
}
```

#### Adding New Validation (Chain of Responsibility)
```java
@Component
public class NewValidationHandler extends OrderValidationHandler {
    
    @Override
    protected ValidationResult doValidate(CreateOrderRequestDTO request) {
        // Perform specific validation
        if (someCondition) {
            return ValidationResult.invalid("Validation failed: reason");
        }
        return ValidationResult.valid();
    }
    
    @Override
    protected String getValidationName() {
        return "NewValidation";
    }
}

// Register in validation chain configuration
@Configuration
public class ValidationConfig {
    @Bean
    public OrderValidationHandler validationChain(
            ItemsValidationHandler itemsHandler,
            ProductExistenceValidationHandler productHandler,
            StockAvailabilityValidationHandler stockHandler,
            NewValidationHandler newHandler) {
        
        itemsHandler.setNext(productHandler);
        productHandler.setNext(stockHandler);
        stockHandler.setNext(newHandler); // Add to chain
        
        return itemsHandler;
    }
}
```

## Testing Strategies

### Unit Testing
```java
@ExtendWith(MockitoExtension.class)
class NewEntityServiceTest {
    
    @Mock
    private NewEntityRepository repository;
    
    @InjectMocks
    private NewEntityService service;
    
    @Test
    void shouldCreateNewEntity() {
        // Given
        CreateNewEntityRequestDTO request = new CreateNewEntityRequestDTO();
        request.setName("Test Entity");
        
        NewEntity savedEntity = NewEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Entity")
                .build();
        
        when(repository.save(any(NewEntity.class))).thenReturn(savedEntity);
        
        // When
        NewEntity result = service.create(request);
        
        // Then
        assertThat(result.getName()).isEqualTo("Test Entity");
        verify(repository).save(any(NewEntity.class));
    }
}
```

### Integration Testing
```java
@SpringBootTest
@Testcontainers
class NewEntityControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateNewEntityViaAPI() {
        // Given
        CreateNewEntityRequestDTO request = new CreateNewEntityRequestDTO();
        request.setName("Integration Test Entity");
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                "/api/new-entities", request, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().isSuccess()).isTrue();
    }
}
```

### Pattern-Specific Testing

#### Testing Proxy Pattern (Caching)
```java
@Test
void shouldCacheResults() {
    // First call - cache miss
    List<Product> products1 = productService.findAll();
    
    // Second call - cache hit
    List<Product> products2 = productService.findAll();
    
    // Verify cache was used
    verify(delegate, times(1)).findAll(); // Only called once
    assertThat(products1).isEqualTo(products2);
}
```

#### Testing Observer Pattern
```java
@Test
void shouldNotifyObserversOnStatusChange() {
    // Given
    Order order = createTestOrder();
    
    // When
    publisher.notifyStatusChange(order, OrderStatus.PENDING, OrderStatus.PAID);
    
    // Then
    verify(observer1).onStatusChanged(order, OrderStatus.PENDING, OrderStatus.PAID);
    verify(observer2).onStatusChanged(order, OrderStatus.PENDING, OrderStatus.PAID);
}
```

## Best Practices

### Code Quality

#### Logging Standards
```java
// Use structured logging with context
log.info("ORDER SERVICE: Creating order for user {} with {} items", 
         userId, request.getItems().size());

// Include pattern context in logs
log.debug("PROXY: Cache hit for product: {}", productId);
log.info("COMMAND: Executing {} - {}", command.getClass().getSimpleName(), 
         command.getDescription());
```

#### Error Handling
```java
// Use centralized message constants
try {
    // Business logic
} catch (SpecificException e) {
    log.error("CONTROLLER: Operation failed: {}", e.getMessage());
    return ResponseUtil.error(e.getMessage(), MessageConstants.OPERATION_FAILED_CODE);
}
```

#### Validation
```java
// Use Bean Validation annotations
@Data
public class CreateOrderRequestDTO {
    @NotNull
    @NotEmpty
    @Valid
    private List<Item> items;
    
    @Data
    public static class Item {
        @NotNull
        private UUID productId;
        
        @Min(1)
        private int quantity;
    }
}
```

### Performance Considerations

#### Database Queries
```java
// Use appropriate fetch strategies
@OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
private List<OrderItem> items;

// Use projections for read-only data
@Query("SELECT new com.example.dto.OrderSummaryDTO(o.id, o.total, o.status) " +
       "FROM Order o WHERE o.userId = :userId")
List<OrderSummaryDTO> findOrderSummariesByUserId(@Param("userId") UUID userId);
```

#### Caching Strategy
```java
// Use appropriate cache TTLs
private static final Duration PRODUCT_CACHE_TTL = Duration.ofMinutes(30);
private static final Duration USER_CACHE_TTL = Duration.ofMinutes(15);
private static final Duration LIST_CACHE_TTL = Duration.ofMinutes(5);
```

### Security Best Practices

#### Input Validation
```java
// Validate all inputs
@PostMapping
public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
        @Valid @RequestBody CreateOrderRequestDTO request) {
    // Validation happens automatically
}
```

#### Authorization
```java
// Use ownership validation
@GetMapping("/{orderId}")
public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(
        @RequestHeader(CommonConstants.AUTH_HEADER) String token,
        @PathVariable UUID orderId) {
    
    ownershipValidationService.validateOrderOwnership(token, orderId);
    // ... rest of method
}
```

### Documentation Standards

#### Code Documentation
```java
/**
 * ADAPTER PATTERN: Adapts Stripe's payment API to our internal payment interface.
 * 
 * This adapter:
 * - Converts our Order data to Stripe's expected format
 * - Handles Stripe-specific error responses
 * - Translates Stripe status codes to our OrderStatus enum
 * 
 * @see AbstractPaymentProcessor for the template method pattern implementation
 */
@Component("stripePayment")
public class StripePaymentAdapter extends AbstractPaymentProcessor {
    // Implementation
}
```

#### API Documentation
```java
/**
 * Creates a new order using the Command Pattern for undoable operations.
 * 
 * The order creation process includes:
 * 1. Validation using Chain of Responsibility pattern
 * 2. Entity creation using Factory pattern
 * 3. State validation using State pattern
 * 4. Event notification using Observer pattern
 * 
 * @param token JWT authorization token
 * @param request order creation details
 * @return standardized API response with created order
 */
@PostMapping
public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
        @RequestHeader(CommonConstants.AUTH_HEADER) String token,
        @RequestBody CreateOrderRequestDTO request) {
    // Implementation
}
```

---

## 🔗 Navigation

| Previous | Home | Next |
|----------|------|------|
| [← Testing Guide](TESTING_GUIDE.md) | [🏠 Home](../README.md) | [Commands Reference →](../COMMANDS.md) |

**Quick Links:**
- [🔧 Development Environment Setup](#development-environment-setup)
- [📁 Code Organization](#code-organization)
- [🎨 Design Pattern Guidelines](#design-pattern-guidelines)
- [➕ Adding New Features](#adding-new-features)
- [🧪 Testing Strategies](#testing-strategies)
- [✨ Best Practices](#best-practices)

[⬆️ Back to Top](#development-guide)