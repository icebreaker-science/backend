# Icebreaker Backend

The default port is `9090`. Open `http://localhost:9090` to find a welcome message and navigate to `http://localhost:9090/swagger-ui.html` for the Swagger UI.

## Setup

**Requirements**

- Java 14+

**Database**

The schema of the database is stored in `database/init.sql`. A docker-compose file is in the same directory providing a simple solution to initialize a database.

**Application Properties**

The (default values of the) application properties are stored in `src/main/resources/application.properties`. There are various ways to overwrite them, including using environment variables. Read the [Spring documentation](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config) for details.
