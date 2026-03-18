This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.


User Request Initiation
A user (Admin, Doctor, or other client like a frontend app) sends a request to the application—either through a web page or an API call.

Controller Handling
The request is received by either an MVC controller (for Thymeleaf-based dashboards) or a REST controller (for API-based modules).

Routing to Service Layer
The controller forwards the request to a common service layer, which contains the business logic of the application.

Business Logic Processing
The service layer processes the request, applies rules, validations, and determines which database or repository is needed.

Repository Interaction
The service layer calls the appropriate repository:

JPA repositories for MySQL data (patients, doctors, appointments, admins)

MongoDB repositories for prescription data

Database Operations
The repositories interact with the databases:

MySQL stores structured relational data using JPA entities

MongoDB stores flexible prescription data as document models

Response Back to User
The processed data is returned from the repository → service → controller, and finally sent back to the user:

As a rendered Thymeleaf page (for dashboards)

Or as a JSON response (for REST APIs)