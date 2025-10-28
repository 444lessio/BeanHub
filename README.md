# BeanHub - UniPA Coffee App ☕

This project is the first assignment for the Web System Design and Architecture (25/26) course at the University of Palermo (UniPA).
It is a web application built with **Spring Boot** and **Thymeleaf**, laying the groundwork for a smart coffee vending machine management platform.

The application is designed to handle 4 different user roles, each with its own dedicated interface and functionalities.


---

## 📌 Assignment 1 Context

Please note that this repository currently contains the solution for **Assignment 1**.

As per the assignment requirements, the primary goal was to build the **static frontend structure** (UI/UX) using Spring Boot and Thymeleaf. All backend logic—such as database connections, user authentication, and dynamic data processing—is **intentionally not implemented** at this stage.

All data displayed in the app is **mock/static** and used as a placeholder for future development in subsequent assignments.

---

## 🚀 How to Run the Project

To run the application locally on your computer:

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/444lessio/BeanHub.git](https://github.com/444lessio/BeanHub.git)
    ```

2.  **Navigate into the directory:**
    ```bash
    cd BeanHub
    ```

3.  **Run the application** (Java 17+ and Maven are required):
    * Via Maven:
        ```bash
        ./mvnw spring-boot:run
        ```
    * Alternatively, open the project in VSC or IntelliJ and run the `BeanhubApplication.java` file.

4.  **Open your browser** and navigate to `http://localhost:8080/`

---

## 💻 Tech Stack

* **Java 21**
* **Spring Boot** (with Spring Web for the server)
* **Thymeleaf** (for server-side rendering of HTML pages)
* **HTML5** & **CSS3**
* **Maven** (for dependency management and build)
* **XML/XSD** (for data structure definition)
* * **JavaScript**

---

## 👨‍💻 User Roles & Features

The application simulates 4 user interfaces, each with static functionalities (for now) that are ready for backend implementation.

### 1. Distributor Screen
The fixed interface for the physical vending machine.
* **URL:** `http://localhost:8080/distributor`
* Displays a mock user and credit.
* Shows selection buttons for beverages and sugar.

### 2. Customer
The end-user who purchases coffee.
* **Login URL:** `http://localhost:8080/`
* **Signup URL:** `http://localhost:8080/customer/signup`
* Allows users to (simulate) sign up and log in.
* The main page (`/customer/main`) allows the user to:
    * Connect to a distributor (via ID).
    * Top up credit (placeholder).
    * Log out.

### 3. Maintenance Attendant
The technician responsible for maintenance.
* **Login URL:** `http://localhost:8080/attendant/login`
* The main page (`/attendant/main`) allows the attendant to:
    * Enter a distributor ID.
    * View its mock status (supply levels, faults).

### 4. System Manager
The platform administrator.
* **Login URL:** `http://localhost:8080/System_manager/login`
* The **Dashboard** (`/System_manager/dashboard`) is the main control panel.
* **Machine Management** (`/System_manager/machines`):
    * Displays the status of all machines (mock data).
    * Simulates adding, removing, and toggling machine status.
* **Attendant Management** (`/System_manager/attendants`):
    * Displays a list of maintenance attendants.
    * Simulates adding and removing attendants.
