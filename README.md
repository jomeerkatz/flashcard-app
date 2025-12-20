# AI Flashcards - Intelligent Difficulty-Based Learning System


> A full-stack flashcard learning application inspired by Anki, featuring AI-powered card generation and an intelligent three-tier difficulty system. Built with modern Java Spring Boot backend and Next.js frontend.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Next.js](https://img.shields.io/badge/Next.js-16-black.svg)](https://nextjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-blue.svg)](https://www.typescriptlang.org/)

## 📹 Demo Video

**[▶️ Watch the Full Application Demo Here](#)** *(Video Placeholder - Upload your demo video and replace this link)*

---

## 🎯 Project Overview

AI Flashcards is a comprehensive learning application that demonstrates production-ready Spring Boot development practices. The application combines traditional flashcard learning with modern AI capabilities, showcasing clean architecture, comprehensive testing strategies, and secure authentication patterns.

**Key Achievement:** This project emphasizes **test-driven development** with full coverage across all layers - a critical skill often missing in junior developer portfolios and highly valued by Hamburg tech companies.

### Why This Project Stands Out

- **Production-Grade Testing:** Comprehensive test suite using Testcontainers, Mockito, and MockMvc
- **Modern Security:** JWT/OAuth2 integration with Keycloak
- **Clean Architecture:** Proper separation of concerns across Controller, Service, and Repository layers
- **AI Integration:** Server-side AI processing with OpenRouter for card generation
- **Scalable Design:** Pagination, caching strategies, and N+1 query prevention
- **Type Safety:** MapStruct for compile-time DTO mapping, eliminating reflection overhead

---

## 🏗️ Architecture & Design Patterns

### Three-Layer Architecture

```
┌─────────────────────────────────────┐
│   CONTROLLER LAYER                  │
│   - REST API Endpoints              │
│   - Request/Response DTOs           │
│   - Input Validation                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   SERVICE LAYER                     │
│   - Business Logic                  │
│   - Transaction Management          │
│   - Authorization Checks            │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   REPOSITORY LAYER                  │
│   - Data Access via Spring Data JPA │
│   - Custom Query Methods            │
│   - Pagination Support              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│   DATABASE (PostgreSQL)             │
└─────────────────────────────────────┘
```

### Entity Relationship Model

```
┌──────────────┐
│     User     │
│──────────────│
│ id (PK)      │◄────┐
│ keycloak_id  │     │
│ created_at   │     │
│ updated_at   │     │
└──────────────┘     │
                     │ @ManyToOne
┌──────────────┐     │     ┌──────────────┐
│    Folder    │     │     │     Card     │
│──────────────│     │     │──────────────│
│ id (PK)      │     │     │ id (PK)      │
│ user_id (FK) │─────┼─────│ user_id (FK) │
│ name         │     │     │ folder_id(FK)│
│ created_at   │     │     │ question     │
│ updated_at   │     │     │ answer       │
└──────────────┘     │     │ status (ENUM)│
                     └─────│ created_at   │
                           │ updated_at   │
                           └──────────────┘
```

**Design Decisions:**
- **Unidirectional Relationships:** `@ManyToOne` without `@OneToMany` to avoid lazy loading issues
- **No JPA Cascades:** Manual cascade operations in service layer for explicit control
- **Sequence Generators:** PostgreSQL-optimized ID generation for better batch insert performance

---

## 🔐 Security Implementation

### JWT/OAuth2 Resource Server

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}
```

**Security Features:**
- **Stateless Authentication:** JWT tokens eliminate server-side session storage
- **Keycloak Integration:** Industry-standard OAuth2/OIDC provider
- **Resource Ownership Validation:** Every request validates user access to resources
- **CORS Configuration:** Secure cross-origin setup for frontend communication

### Authorization Pattern

```java
// Every service method validates resource ownership
boolean userHasAccess = folderRepository
    .existsByUserIdAndName(userId, folderName);

if (!userHasAccess) {
    throw new FolderAccessDeniedException("Access denied!");
}
```

---

## 🧪 Comprehensive Testing Strategy

### Why Testing Matters for Junior Positions

**This is the differentiator.** Most junior developer portfolios lack proper testing. This project demonstrates understanding of:
- Different testing strategies per layer
- Test isolation and independence
- Mocking vs integration testing decisions
- Production-ready test practices

### Repository Layer - Integration Tests

**Technology:** `@DataJpaTest` + Testcontainers + PostgreSQL

```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Testcontainers
public class CardRepositoryIntegrationTests {
    
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:17");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    public void testThatFindsByStatusReturnsCorrectCards() {
        // Test with real PostgreSQL database
    }
}
```

**Why Testcontainers over H2:**
- Tests run against actual PostgreSQL (production parity)
- Eliminates SQL dialect differences
- Demonstrates modern testing practices
- Shows understanding of Docker/containerization

### Service Layer - Unit Tests

**Technology:** `@ExtendWith(MockitoExtension.class)` + Mockito

```java
@ExtendWith(MockitoExtension.class)
public class UserServiceImplJUnitTests {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    public void testThatHandlesRaceConditionGracefully() {
        // Arrange
        when(userRepository.findByKeycloakId(keycloakId))
            .thenReturn(Optional.empty())
            .thenReturn(Optional.of(user));
        
        when(userRepository.save(any()))
            .thenThrow(new DataIntegrityViolationException("Duplicate"));
        
        // Act
        User result = userService.createOrFindUser(user);
        
        // Assert
        assertThat(result).isEqualTo(user);
        verify(userRepository, times(2)).findByKeycloakId(keycloakId);
    }
}
```

**Testing Focus:**
- Business logic isolation
- Edge case handling (race conditions, exceptions)
- Behavior verification with `verify()`
- Fast execution (no database/HTTP overhead)

### Controller Layer - Integration Tests

**Technology:** `@WebMvcTest` + MockMvc

```java
@WebMvcTest(UserController.class)
public class UserControllerJUnitTests {
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testCreateUserReturnsUserDto() throws Exception {
        when(userService.createOrFindUser(any()))
            .thenReturn(user);
        
        mockMvc.perform(post("/api/users")
                .with(jwt().jwt(jwt -> jwt.subject(keycloakId))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId));
    }
}
```

**Testing Focus:**
- HTTP layer validation (status codes, headers, JSON)
- JWT authentication integration
- Request/response serialization
- Exception handling (@ControllerAdvice)

### Test Coverage Summary

| Layer | Test Type | Technology | Coverage |
|-------|-----------|------------|----------|
| **Repository** | Integration | Testcontainers + PostgreSQL | All custom queries |
| **Service** | Unit | Mockito | Business logic + edge cases |
| **Controller** | Integration | MockMvc | All endpoints + error handling |

---

## 🤖 AI Integration

### Server-Side Processing Decision

**Architecture Choice:** AI processing happens server-side via Next.js API routes, not in the Spring Boot backend.

**Why This Approach:**
- **Security:** API keys never exposed to client
- **Cost Control:** Server-side rate limiting and monitoring
- **Flexibility:** Can switch AI providers without backend changes
- **Backend Integration:** Direct card persistence after generation

### OpenRouter Integration

```typescript
// app/api/generate-cards/route.ts
const openRouterResponse = await fetch(
  "https://openrouter.ai/api/v1/chat/completions",
  {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${OPENROUTER_API_KEY}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model: "openai/gpt-5-nano", // Cost-optimized model
      messages: [
        {
          role: "system",
          content: "You are a flashcard generator..."
        },
        {
          role: "user",
          content: `Create flashcards about: ${prompt}`
        }
      ],
      response_format: { type: "json_object" } // Structured output
    })
  }
);
```

**Technical Highlights:**
- **Model Selection:** GPT-5-Nano for cost efficiency
- **Structured Output:** JSON schema validation for reliable parsing
- **Bulk Operations:** Spring Boot bulk insert endpoint for performance
- **Error Handling:** Multi-layer validation (API → JSON → Backend)

---

## 📊 Learning System Architecture

### Three-Tier Difficulty System

```
┌─────────────────────────────────────────────┐
│  BAD (47)      MEDIUM (23)      GOOD (12)  │
├─────────────────────────────────────────────┤
│  [Learn BAD]   [Learn MEDIUM]   [Learn GOOD]│
└─────────────────────────────────────────────┘
```

**User Flow:**
1. User selects difficulty tier (BAD/MEDIUM/GOOD)
2. System fetches 20 cards, displays first 10
3. Second 10 cards cached in frontend
4. Pagination with deterministic ordering
5. Random shuffle within each batch to prevent memorization

### Backend Pagination Strategy

```java
@GetMapping("/{folderId}/learn-cards/{status}")
public Page<CardDto> getCardsForLearning(
    @AuthenticationPrincipal Jwt jwt,
    @PathVariable Long folderId,
    @PathVariable CardStatus status,
    @PageableDefault(size = 20) Pageable pageable
) {
    return cardService.getCardsByStatus(user, folderId, status, pageable)
        .map(cardMapper::toDto);
}
```

**Design Decisions:**
- **Deterministic Pagination:** Page numbers provide predictable progress tracking
- **Server-Side Filtering:** Status-based queries for performance
- **Frontend Caching:** Prefetch 20, display 10 strategy reduces API calls
- **Random Shuffle:** Applied in frontend to cards within same batch

---

## 🛠️ Technical Stack

### Backend Technologies

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Framework** | Spring Boot | 4.0.0 | Application framework |
| **Language** | Java | 21 | Programming language |
| **Security** | Spring Security OAuth2 | 4.0.0 | JWT/Keycloak integration |
| **Database** | PostgreSQL | 17 | Production database |
| **ORM** | Hibernate (via Spring Data JPA) | 6.4.x | Object-relational mapping |
| **DTO Mapping** | MapStruct | 1.6.3 | Compile-time bean mapping |
| **Boilerplate** | Lombok | 1.18.32 | Code generation |
| **Testing** | Testcontainers | 2.0.2 | Docker-based integration tests |
| **Testing** | Mockito | 5.x | Mocking framework |
| **Testing** | AssertJ | 3.x | Fluent assertions |

### Frontend Technologies

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Framework** | Next.js | 16 | React framework |
| **Language** | TypeScript | 5 | Type safety |
| **Authentication** | NextAuth.js | 5.0.0 | OAuth2/OIDC client |
| **Styling** | TailwindCSS | 4 | Utility-first CSS |
| **AI Integration** | OpenRouter | - | AI model aggregator |

---

## 💡 Key Technical Concepts Demonstrated

### 1. Clean Architecture & SOLID Principles

- **Single Responsibility:** Each layer has one job
- **Dependency Inversion:** Service depends on Repository interface
- **Interface Segregation:** Separate interfaces per service

### 2. Spring Boot Best Practices

✅ **Constructor Injection** (preferred over field injection)
```java
@AllArgsConstructor // Lombok generates constructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
}
```

✅ **Transaction Management**
```java
@Transactional
public void deleteFolder(User user, Long folderId) {
    cardRepository.deleteAllByFolderId(folderId); // Explicit cascade
    folderRepository.deleteById(folderId);
}
```

✅ **Exception Handling** (Global @ControllerAdvice)
```java
@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFound(FolderNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorDto(ex.getMessage()));
    }
}
```

### 3. Data Access Patterns

✅ **Custom Query Methods** via Spring Data JPA method naming
```java
public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByUserIdAndFolderIdAndStatus(
        Long userId, Long folderId, CardStatus status, Pageable pageable
    );
    Long countByFolderId(Long folderId);
}
```

✅ **N+1 Query Prevention**
- No bidirectional relationships
- Explicit WHERE clauses instead of JOIN FETCH
- DTO projections to avoid loading full entities

✅ **Pagination** for scalability
```java
@PageableDefault(size = 20, page = 0) Pageable pageable
```

### 4. Security Patterns

✅ **Resource Ownership Validation**
```java
boolean hasAccess = folderRepository.existsByUserIdAndName(userId, folderName);
```

✅ **Stateless JWT Authentication**
- No server-side sessions
- Token validation on every request
- Automatic token refresh

### 5. Testing Strategies

✅ **Test Data Builders** for reusability
```java
public final class DataUtil {
    public static User getUserExample1() {
        return User.builder()
            .keycloakId("test-id-1")
            .createdAt(LocalDateTime.now())
            .build();
    }
}
```

✅ **Testcontainers** for production parity
✅ **Mockito** for isolated unit tests
✅ **MockMvc** for HTTP layer testing

---

## 📈 What This Project Demonstrates to Employers

### Technical Skills

- ✅ Modern Java (21) with latest Spring Boot (4.0)
- ✅ RESTful API design with proper HTTP semantics
- ✅ JWT/OAuth2 security implementation
- ✅ Comprehensive testing (unit, integration, E2E concepts)
- ✅ Database design and JPA/Hibernate proficiency
- ✅ Docker/Testcontainers for testing
- ✅ TypeScript and modern frontend frameworks
- ✅ AI integration and prompt engineering

### Professional Practices

- ✅ Clean code and architecture
- ✅ Git workflow (feature branches, proper commits)
- ✅ Documentation and code comments
- ✅ Error handling and validation
- ✅ Security-first mindset
- ✅ Performance considerations (pagination, caching, N+1 prevention)

### Problem-Solving Abilities

- ✅ Handled race conditions (concurrent user creation)
- ✅ Implemented complex business logic (learning system)
- ✅ Integrated third-party services (Keycloak, OpenRouter)
- ✅ Designed scalable data models
- ✅ Built user-friendly error messages

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- PostgreSQL 17
- Keycloak instance
- Node.js 20+
- Docker (for Testcontainers)

### Backend Setup

```bash
cd ai-flashcards-backend

# Configure application.properties
export DB_URL=jdbc:postgresql://localhost:5432/flashcards
export DB_USERNAME=flashcards
export DB_PASSWORD=flashcards
export JWT_ISSUER_URI=http://localhost:9090/realms/flashcards-app

# Run tests
./mvnw test

# Start application
./mvnw spring-boot:run
```

### Frontend Setup

```bash
cd ai-flashcards-frontend

# Install dependencies
npm install

# Configure environment
# Create .env.local with:
# KEYCLOAK_CLIENT_ID=your-client-id
# KEYCLOAK_CLIENT_SECRET=your-client-secret
# KEYCLOAK_ISSUER=http://localhost:9090/realms/flashcards-app
# OPENROUTER_API_KEY=your-api-key

# Run development server
npm run dev
```

---

## 📝 API Documentation

### Authentication

All endpoints (except health checks) require JWT Bearer token:

```http
Authorization: Bearer <jwt-token>
```

### Core Endpoints

#### Users
- `POST /api/users` - Create or find user by Keycloak ID

#### Folders
- `GET /api/folders?page=0&size=5` - List folders (paginated)
- `POST /api/folders` - Create folder
- `PUT /api/folders/{id}` - Update folder
- `DELETE /api/folders/{id}` - Delete folder (cascades to cards)

#### Cards
- `GET /api/folders/{id}/cards?page=0&size=20` - List cards
- `POST /api/folders/{id}/cards` - Create card
- `POST /api/folders/{id}/cards/bulk` - Bulk create (AI generation)
- `PUT /api/folders/{id}/cards/{cardId}` - Update card
- `DELETE /api/folders/{id}/cards/{cardId}` - Delete card

#### Learning
- `GET /api/folders/{id}/learn-cards/{status}?page=0&size=20` - Get cards by difficulty
- `PUT /api/folders/{id}/update-learning-card/{cardId}/{status}` - Update card status
- `GET /api/folders/{id}/cards/count` - Get card count

---

## 🎓 What I Learned

### Technical Growth

- **Testing is Critical:** Comprehensive tests give confidence to refactor
- **Architecture Matters:** Clean separation makes debugging easier
- **Security by Default:** Always validate resource ownership
- **Type Safety:** MapStruct caught bugs at compile-time
- **Container Testing:** Testcontainers eliminated H2/PostgreSQL discrepancies

### Project Management

- **Feature Branches:** Isolated work prevents merge conflicts
- **Commit Messages:** Descriptive commits help code review
- **Documentation:** README helps others (and future me) understand decisions

---

## 🤝 Contributing

This is a portfolio project, but feedback is welcome! Feel free to open issues for questions or suggestions.

---
