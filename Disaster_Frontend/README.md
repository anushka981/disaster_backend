# 🚨 Disaster Response Coordination System

A web-based Disaster Response Coordination System that helps manage disaster reports, emergency help requests, volunteers, and response activities through a centralized platform.

The system allows users to report disasters, request help, volunteers to manage assigned tasks, and administrators to monitor and coordinate disaster response operations.

---

## 📌 Features

### 👤 User Features

* Submit disaster reports
* Submit emergency help requests
* View response status
* Simple and user-friendly interface

### 🛡️ Admin Features

* Admin dashboard
* View all disaster reports
* View help requests
* Assign volunteers to disaster reports
* Update disaster status
* Dashboard statistics:

  * Total Disaster Reports
  * Total Help Requests
  * Active Volunteers

### 🦺 Volunteer Features

* Volunteer registration/login
* View assigned disaster tasks
* Manage assigned responsibilities

### 🔍 Additional Features

* Search disaster reports
* Image upload support
* Responsive UI using Bootstrap

---

## 🛠️ Tech Stack

### Backend

* Java
* Spring Boot
* Spring MVC
* Spring Data MongoDB
* Maven

### Database

* MongoDB

### Frontend

* HTML5
* CSS3
* JavaScript
* Bootstrap 5

### Tools

* IntelliJ IDEA
* VS Code
* Postman
* Git & GitHub

---

## 🏗️ Project Architecture

```
Disaster Response System

Frontend
   |
   | REST API
   |
Spring Boot Backend
   |
MongoDB Database
```

---

## 📂 Project Structure

```
Disaster-Backend
│
├── controller
│   ├── AuthController
│   ├── DisasterController
│   └── HelpRequestController
│
├── model
│   ├── DisasterReport
│   ├── HelpRequest
│   └── Volunteer
│
├── repository
│
├── service
│
└── DisasterBackendApplication.java
```

---

## ⚙️ Installation & Setup

### 1. Clone Repository

```bash
git clone <your-github-repository-url>
```

### 2. Configure MongoDB

Make sure MongoDB is running locally.

Update `application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/disasterdb
spring.data.mongodb.database=disasterdb
```

### 3. Run Backend

Open the project and run:

```
DisasterBackendApplication.java
```

Backend will start on:

```
http://localhost:8080
```

---

## 🔗 API Endpoints

### Disaster Reports

| Method | Endpoint     | Description            |
| ------ | ------------ | ---------------------- |
| POST   | /report      | Create disaster report |
| GET    | /reports     | Get all reports        |
| PUT    | /report/{id} | Update report status   |

---

### Help Requests

| Method | Endpoint | Description         |
| ------ | -------- | ------------------- |
| POST   | /help    | Create help request |
| GET    | /helps   | View help requests  |

---

### Volunteer

| Method | Endpoint | Description              |
| ------ | -------- | ------------------------ |
| POST   | /signup  | Register volunteer/admin |
| POST   | /login   | User login               |

---

## 📸 Screenshots

(Add project screenshots here)

Example:

* User Report Page
* Admin Dashboard
* Volunteer Dashboard
* Help Request Page

---

## 🎯 Future Improvements

* Real-time disaster alerts
* Live disaster location tracking
* SMS/Email notifications
* Advanced role-based security
* Cloud deployment

---

## 👩‍💻 Author

**Anushka Bansal**

Computer Science & Engineering

---

## ⭐ Project Purpose

This project aims to improve disaster management by connecting affected users, volunteers, and administrators on a single platform for faster and more organized emergency response.
