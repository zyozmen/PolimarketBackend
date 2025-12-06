# PoliMarket Backend

A Spring Boot 3.4.1 backend service built with Java 17 and Maven.

## Prerequisites

- Java 17 or higher
- Maven 3.8.1 or higher

## Project Structure

```
PolimarketBack/
├── src/
│   ├── main/
│   │   ├── java/com/polimarket/
│   │   │   ├── PolimarketApplication.java
│   │   │   └── controller/
│   │   │       └── HealthController.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/polimarket/
├── pom.xml
└── README.md
```

## Building the Project

```bash
mvn clean package
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## Health Check

Check if the application is running:

```bash
curl http://localhost:8080/api/health
```

## Features

- **Spring Boot 3.4.1** - Latest stable version
- **Java 17** - LTS version
- **Spring Web** - REST API support
- **Spring Data JPA** - Database operations
- **Spring Security** - Security framework
- **H2 Database** - In-memory database for development
- **Lombok** - Reduce boilerplate code
- **Maven** - Build and dependency management

## Database

The application uses H2 in-memory database by default. Access the H2 console at:
```
http://localhost:8080/api/h2-console
```

## Development

### Build
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Package
```bash
mvn clean package
```

## Troubleshooting

### Maven not found
Ensure Maven is installed and added to your PATH.

### Port already in use
Change the port in `src/main/resources/application.yml`:
```yaml
server:
  port: 8081  # or any available port
```

## License

MIT
