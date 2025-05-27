# PDF Report Generator

A Spring Boot application for generating PDF reports using templates and storing report metadata in a database.

## Features

- PDF generation using FreeMarker templates
- Report metadata storage in H2 database
- RESTful API endpoints for report management
- Report status tracking
- Audit trail for report creation and updates

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
cd pdf-report-docs
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Database Access

The application uses H2 in-memory database. You can access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:reportdb`
- Username: `sa`
- Password: `password`

## Project Structure

```
src/main/java/com/example/demo/
├── controller/    # REST controllers
├── dto/          # Data Transfer Objects
├── entity/       # JPA entities
├── repository/   # Database repositories
├── service/      # Business logic
└── util/         # Utility classes
```

## API Endpoints

- `POST /api/pdf/generate` - Create a new report


## Development

### Building

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

