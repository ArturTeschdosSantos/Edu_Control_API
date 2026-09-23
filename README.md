# Edu-Control

Edu-Control is a personal project developed with **Java and Spring Boot** to manage student access to a school cafeteria.

The system is designed to register students, organize them by school period, and control cafeteria entry and exit.

## Technologies

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Hibernate
- Maven
- Lombok
- Git / GitHub

## Architecture

The project follows a layered structure:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

## Main Features

- Student registration
- Student search
- Organization by school period
- Cafeteria entry and exit control
- Access history
- Prevention of duplicate entries

## Current API

```http
GET /students
```

Returns all students.

```http
GET /students/{id}
```

Returns a student by UUID.

## Planned Endpoints

```http
POST /students
PUT /students/{id}
DELETE /students/{id}

POST /cafeteria/entry/{registration}
POST /cafeteria/exit/{registration}
GET /cafeteria/history
```

## Database

The project uses **PostgreSQL** with Spring Data JPA.

Student data includes information such as:

```text
id
name
registration
classroom
period
```

Each student will also be able to have multiple cafeteria access records.

## Current Status

The project is under development.

Current focus:

- Student entity
- Student repository
- Student controller
- PostgreSQL integration

Next steps:

- Complete Student CRUD
- Add Service layer
- Implement cafeteria entry and exit
- Add validations and error handling
- Create access history

## Learning Goals

This project is being developed to improve my knowledge of:

- Java
- Spring Boot
- REST APIs
- JPA
- PostgreSQL
- Layered architecture
- Git and GitHub
