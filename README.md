# AquariumManager Backend v2.1.1

A Java-based REST API for managing aquarium systems. Built for a school assignment.

## Features

- **Aquarium Management:** Create, Read, Update and Delete aquariums
- **Inhabitants:** Manage fish and other aquatic life
- **Accessories:** Manage filters, lighting and thermostats
- **Ornaments:** Manage decorative ornaments like castles
- **Authentication:** JWT-based authentication system
- **Database:** Hosted database (NEON-DB) using PostgreSQL

## Technology Stack

- Java 17
- Jakarta EE 10 (Web API, Transaction API)
- Jersey (JAX-RS) for RESTful services
- Jackson for JSON serialization
- PostgreSQL (JDBC driver)
- SLF4J (Simple Logging)
- Auth0 Java JWT (authentication)
- BCrypt (password hashing)
- Lombok (code generation)
- JUnit 5 & Mockito (testing)
- Maven (build tool)
- Apache Tomcat 10 (embedded, via Cargo plugin)

### Database

The database is hosted online using Neon-DB, a service offering free hosting of databases. Works perfectly with both Railway for backend hosting and Vercel for frontend hosting.

## Documentation

Visit the [API url](https://web-production-8a8d.up.railway.app/api/) of the backend to see the documentation.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details..
