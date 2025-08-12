# Public Transit Fleet Management System (PTFMS)

## Overview
The Public Transit Fleet Management System (PTFMS) is a web-based application designed for public transit agencies to monitor, track, and optimize operations of buses, electric light rail, and diesel-electric trains.

## Architecture
This project implements a 3-tier architecture:
- **Presentation Layer**: JSP views and Servlets
- **Business Layer**: Service classes and business logic
- **Data Layer**: DAO classes and database operations

## Design Patterns Implemented

### Required Patterns:
1. **DAO (Data Access Object)**: 
   - `UserDAO.java` - Handles user data operations
   - `VehicleDAO.java` - Handles vehicle data operations
   - `DatabaseConnection.java` - Manages database connections

2. **Builder**: 
   - `VehicleBuilder.java` - Creates VehicleDTO objects using builder pattern

### Additional Patterns (4 selected):
3. **Strategy**: 
   - `UserService.java` - Implements different authentication strategies

4. **Command**: 
   - `Command.java` - Command interface
   - `AddVehicleCommand.java` - Concrete command implementation

5. **Simple Factory**: 
   - Implemented in controller classes for object creation

6. **Adapter**: 
   - Database connection adapter pattern in `DatabaseConnection.java`

## Project Structure
```
src/main/java/
├── transferobjects/          # DTOs for data transfer
│   ├── UserDTO.java
│   ├── VehicleDTO.java
│   ├── MaintenanceDTO.java
│   └── FuelLogDTO.java
├── dataaccesslayer/          # Data layer
│   ├── DatabaseConnection.java
│   ├── UserDAO.java
│   └── VehicleDAO.java
├── businesslayer/            # Business layer
│   ├── UserService.java
│   ├── VehicleBuilder.java
│   ├── Command.java
│   └── AddVehicleCommand.java
├── controllers/              # Controller layer
│   └── PTFMSController.java
└── servlets/                # Presentation layer
    ├── LoginServlet.java
    ├── RegisterServlet.java
    └── VehicleServlet.java

src/main/webapp/
├── index.html
└── WEB-INF/
    └── views/
        ├── login.jsp
        ├── register.jsp
        └── vehicles.jsp
```

## Database Schema
The application uses MySQL 8.0.40 with the following tables:
- `users` - User authentication and roles
- `vehicles` - Vehicle information
- `maintenance` - Maintenance records
- `fuel_logs` - Fuel consumption tracking

## Technology Stack
- Java 21
- Jakarta EE 10
- MySQL 8.0.40
- Apache Tomcat 9.0.90+
- Maven for build management

## Functional Requirements Implemented
- FR-01: User Registration & Authentication
- FR-02: Basic Vehicle Management
- Foundation for GPS Tracking, Fuel Monitoring, Maintenance Alerts, and Reporting

## Build and Deployment
1. Ensure MySQL is running with the PTFMS database created
2. Update database connection properties in `src/main/java/com/mycompany/FinalProject-PTFMS/resources/database.properties`
3. Build with Maven: `mvn clean package`
4. Deploy the generated WAR file to Tomcat
5. Access the application at: `http://localhost:8080/FinalProject-PTFMS`

## Design Pattern Details

### DAO Pattern
- Separates data access logic from business logic
- Provides a clean interface for database operations
- Handles connection management and exception handling

### Builder Pattern
- Used for creating complex VehicleDTO objects
- Provides fluent interface for object construction
- Ensures consistent object creation

### Command Pattern
- Encapsulates requests as objects
- Allows for undo operations
- Provides loose coupling between invoker and receiver

### Strategy Pattern
- Implemented in UserService for different authentication methods
- Allows runtime algorithm selection
- Provides flexibility for future authentication strategies

## Author
Louis Bertrand Ntwali
