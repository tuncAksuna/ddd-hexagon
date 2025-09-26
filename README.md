### Layer Responsibilities

| Layer | Responsibility | Dependencies |
|-------|----------------|--------------|
| **Domain** | Business rules, entities, domain services | ❌ No external dependencies |
| **Application** | Use cases, orchestration, ports definition | → Domain |
| **Infrastructure** | External systems, databases, message queues | → Application → Domain |
| **API** | REST endpoints, request/response handling | → Application → Domain |

- **Domain** → Zero external dependencies
- **Application** → Depends only on Domain
- **Infrastructure** → Depends on Application & Domain
- **API** → Depends on Application & Domain

## 🛠️ Technology Stack

### Backend Framework
- **Java 17**
- **Spring Boot 3.4.10**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database abstraction layer

### Database & Caching
- **PostgreSQL 16.0** - Primary relational database
- **Redis 7** - Caching and session storage
- **Spring Data Redis** - Redis integration

### Messaging & Events
- **Apache Kafka** - Event streaming platform
- **Spring Kafka** - Kafka integration for event-driven architecture

### Development Tools
- **Maven Multi-Module** - Dependency management & build tool
- **Lombok** - Boilerplate code reduction
- **Docker & Docker Compose** - Containerization and orchestration

### ✅ Ports & Adapters Implementation

**Input Ports (Use Cases):**
- `GetProductListPort` - Retrieve all products
- `GetProductByIdPort` - Retrieve specific product

**Output Ports (Repository Interfaces):**
- `ProductDataJpaRepositoryPort` - Database operations contract

**Input Adapters (Controllers):**
- `ProductController` - REST API implementation

**Output Adapters (Repository Implementations):**
- `ProductDataJpaRepositoryAdapter` - PostgreSQL implementation

### Design Patterns Used
- **Hexagonal Architecture (Ports & Adapters)**
- **Repository Pattern**
- **Adapter Pattern**
- **Dependency Injection**
- **Builder Pattern** (Lombok @Builder)

  
### Code Quality Features
- **Aspect-Oriented Programming** for cross-cutting concerns
- **Global Exception Handling** with `@RestControllerAdvice`
- **Method-Level Security** with role-based access control
- **Clean separation of concerns** across all layers


## 🤝 Contributing

I welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request
  

## 🙏 Acknowledgments

- **Robert C. Martin (Uncle Bob)** - Clean Architecture concept
- **Alistair Cockburn** - Hexagonal Architecture pattern
- **Lemi Orhan Ergin and Alican Akkuş** - https://www.youtube.com/watch?v=jx49C380EgI / 
---

⭐ **If you found this project helpful, please give it a star!**

📫 **Contact**: [aksuna.tunc@gmail.com](mailto:aksuna.tunc@egmail.com)

🔗 **Connect with me**: [LinkedIn Profile](https://www.linkedin.com/in/cem-tunç-aksuna-012b31205)

<img width="1272" height="832" alt="arch" src="https://github.com/user-attachments/assets/afc9d609-21d7-48ff-9ee4-303f462e7839" />
