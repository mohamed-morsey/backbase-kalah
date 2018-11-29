# Backbase Kalah
Simple game for Backbase Kalah coding challenge.

## Instructions to Build and Start the Backbase-Kalah Application

## Prerequisites
1. JDK 1.8.
2. Maven 3.

## Building the Backbase-Kalah Application
1. Go to project folder.
2. Run command **"mvn clean install"** to build the application including unit and integration tests.

## Starting up the Application
1. Go to project folder.
2. Run command **"cd kalah"**.
3. Run command **"mvn spring-boot:run"** to start the application.

The application starts and it is available at **"http://localhost:8080/games"**.

## Supported Operating Systems
The application has been tested on:

1. Ubuntu 16.04 LTS.
2. Windows 7 Ultimate.

## Design and Implementation Details
The application is a web based application developed based on Spring Boot framework.
Currently, the application supports managing game entities.
However, the application can be extended easily to support more entities, e.g. players.

The application has the following features:

1. **Extensibility**: A generic interface called *"CrudService"* that incorporates the basic CRUD (create, read, update, 
and delete) operations. This interface is implemented by *"GameService"* and *"BoardService"* 
in order to support CRUD operations for games and boards. If a new entity is added and needs to be supported
e.g. player, a service is created for this entity and should implement that interface.
2. **Model-View-Controller(MVC)**: The application adheres to the MVC pattern via Spring framework.
3. **Separation of Concerns (SoC)**: The application achieves separation of concerns (SoC) via applying 
Data Transfer Object (DTO) pattern. For instance, for class *"Game"* DTO classes called *"GameDto"*
and *"GameStatusDto"* are created.
4. **Database**: H2 database is used in embedded mode to provide data storage. 
5. **Exception Handling**: The application defines an exception handler, called *"KalahGameExceptionHandler*", 
which catches the exceptions thrown and returns the appropriate HTTP status code along with an error message.
6. **Testing**: Unit and integration tests are used to cover the various application features.
7. **Code Quality**: The code quality is inspected and checked with [SonarQube](https://sonarcloud.io/about/sq).

## Design Objectives
The main objectives of this design are:

1. **Scalability**: We are using H2 database in embedded mode but it is quite simple to port it to 
another database system running in client-server mode.
2. **Extensibility**: Minor changes are required to support more entities, e.g. players.
3. **Robustness**: Using unit and integration tests to cover as many test scenarios as possible
insures the robustness of the system.