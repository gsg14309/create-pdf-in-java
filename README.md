# PDF Generation Service

A Spring Boot application that provides PDF generation capabilities using FreeMarker templates and OpenPDF.

## Features

- PDF generation from HTML templates using FreeMarker
- Support for dynamic content rendering
- Integration with OpenPDF for PDF creation
- Flying Saucer for HTML to PDF conversion
- H2 in-memory database for data persistence (TBD)
- RESTful API endpoints for PDF generation

## Technology Stack

- Java 17
- Spring Boot 3.2.4
- Spring Data JPA
- FreeMarker 2.3.32
- OpenPDF 1.3.30
- Flying Saucer 9.3.1
- H2 Database
- Lombok
- Maven

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── demo/
│   │               ├── controller/    # REST controllers
│   │               ├── service/       # Business logic
│   │               ├── repository/    # Data access
│   │               ├── model/         # Entity classes
│   │               ├── dto/           # Data Transfer Objects
│   │               ├── mapper/        # Object mappers
│   │               └── util/          # Utility classes
│   └── resources/
│       ├── templates/                 # FreeMarker templates
│       ├── static/                    # Static resources
│       └── application.properties     # Application configuration
└── test/                             # Test classes
```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```bash
   cd pdf-generator
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## Configuration

The application can be configured through `application.properties`:

- Server port
- Database settings
- Template locations
- PDF generation settings

## API Endpoints

### PDF Generation
- `POST /api/pdf/generate` - Generate PDF from template
- `GET /api/pdf/templates` - List available templates (TBD)
- `GET /api/pdf/{id}` - Download generated PDF (TBD)

## Development

### Adding New Templates

1. Create a new FreeMarker template in `src/main/resources/templates/`
2. Use FreeMarker syntax for dynamic content
3. Register the template in the template service

### Building PDFs

The project uses:
- FreeMarker for template processing
- OpenPDF for PDF generation
- Flying Saucer for HTML to PDF conversion

## Testing

Run tests using Maven: TBD
```bash
mvn test
```

The project includes:
- Unit tests
- Integration tests
- Controller tests

## Dependencies

Key dependencies are managed in `pom.xml`:
- Spring Boot Starters (Web, Data JPA)
- FreeMarker for templating
- OpenPDF for PDF generation
- Flying Saucer for HTML to PDF conversion
- H2 Database for development (TBD)
- Lombok for reducing boilerplate code

