# Running the Application and Interacting with API Endpoints

## Prerequisites
- Java Development Kit (JDK) version 17 or higher installed on your system
- Maven installed on your system
- Git installed on your system (optional if you're not cloning the repository)
- Postman or any other API testing tool (optional for testing API endpoints)

## Steps to Run the Application

1. **Clone the Repository (Optional)**:
   If you haven't already cloned the repository, you can do so by running the following command in your terminal or command prompt:
   ```bash
   git clone <repository_url>
2. **Navigate to the Project Directory**:
   Change your current directory to the project directory:
   ```bash
   cd <project_directory>

3. **Setting up the Database**:
   Before running the application, ensure that you have set up the database. You can follow these steps:
    - **Create Database**: Open your MySQL database management tool and execute the following SQL query to create the database:
      ```sql
      CREATE DATABASE library_db;
      ```
    - **Database Configuration**: If necessary, configure the database connection properties in the `application.properties` file of the Spring Boot application. Ensure that the following properties are correctly set:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/library_db
      spring.datasource.username=library_db
      spring.datasource.password=
      ```
      Adjust the URL, username, and password according to your database setup.

4. **Build the Project**:
   Use your preferred editor (intellij idea recommended) to build and run, or
   Run the following Maven command to build the project:
   ```bash
   mvn clean install
5. **Run the Application**:
   After the build is successful, run the following command to start the application:
   ```bash
   java -jar target/<jar_filename>.jar
6. **Accessing the Application**:
   Once the application is running, it should be accessible at `http://localhost:8080`.

## Interacting with API Endpoints
[Postman Documentation](https://documenter.getpostman.com/view/14571079/2sA3JT3JdT): You can find detailed documentation for the API endpoints in the Postman collection. This documentation includes descriptions, request examples, and response schemas.

### Book API Endpoints

- **Get All Books**: `GET http://localhost:8080/api/books`
- **Get Book by ID**: `GET http://localhost:8080/api/books/{id}`
- **Add a Book**: `POST http://localhost:8080/api/books`
- **Update a Book**: `PUT http://localhost:8080/api/books/{id}`
- **Delete a Book**: `DELETE http://localhost:8080/api/books/{id}`

### Patron API Endpoints

- **Get All Patrons**: `GET http://localhost:8080/api/patrons`
- **Get Patron by ID**: `GET http://localhost:8080/api/patrons/{id}`
- **Add a Patron**: `POST http://localhost:8080/api/patrons`
- **Update a Patron**: `PUT http://localhost:8080/api/patrons/{id}`
- **Delete a Patron**: `DELETE http://localhost:8080/api/patrons/{id}`

### Borrowing API Endpoints

- **Borrow a Book**: `POST http://localhost:8080/api/borrow/{bookId}/patron/{patronId}`
- **Return a Book**: `PUT http://localhost:8080/api/return/{bookId}/patron/{patronId}`

## Authentication (Not Implemented Yet)
Add request header
```bash
Authorization: Bearer <token>
```

## Testing the API Endpoints

You can use [Postman](https://documenter.getpostman.com/view/14571079/2sA3JT3JdT) or run test in IDE.
Or run:
```bash
mvn test 
```

That's it! You should now be able to run the application, interact with its API endpoints, and handle authentication if implemented.


