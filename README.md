<div align="center">
  <h1>🚀 Skill-Centric Job Matchmaking Portal</h1>
  <p><i>A modern, full-stack Spring Boot application that intelligently connects job seekers with employers based on their skill sets.</i></p>

  <!-- Badges -->
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.4-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java" alt="Java" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql" alt="MySQL" />
  <img src="https://img.shields.io/badge/Thymeleaf-Template-green?style=for-the-badge&logo=thymeleaf" alt="Thymeleaf" />
</div>

<br/>

## 🌟 Overview

The **Skill Development and Job Connect Portal** is designed to eliminate the noise in traditional job hunting. Instead of standard keyword searches, our platform utilizes an **Intersection-based Skill Scoring Algorithm** to rank and match candidates with jobs that perfectly align with their technical capabilities.

## ✨ Key Features

- 🧠 **Smart Matchmaking**: Calculates a compatibility score based on the intersection of a user's skills and a job's required skills.
- 🚦 **Role-Based Access Control**: Distinct profiles and capabilities for **Seekers**, **Employers**, and **Admins**.
- 📬 **Asynchronous Job Alerts**: Nightly background workers (`@Async` and `@Scheduled`) evaluate matches and dispatch job alerts without blocking the main threads.
- 📊 **Admin Dashboard**: Comprehensive statistics and platform moderation interface.
- 🔒 **Spring Security**: BCrypt password hashing, secure session management, and role-based route protection.

---

## 🏗️ Architecture & Tech Stack

### Backend
- **Core Framework**: Java 17, Spring Boot 3.2.4
- **Security**: Spring Security 6
- **Persistence**: Spring Data JPA, Hibernate, JDBC
- **Database**: H2 (Development) / MySQL 8 (Production)

### Frontend
- **Templating**: Thymeleaf (with Spring Security Extras)
- **Design & Styling**: Custom CSS architecture with responsive layout components.

---

## 👥 User Roles

### 🧑‍💻 Seeker
- Create a profile and manage technical skills.
- View personalized job recommendations ranked by match percentage.
- Submit applications and track application status.

### 🏢 Employer
- Build a robust company profile.
- Post job listings specifying explicitly required skills.
- View applicants, filter by match score, and update application statuses.

### 🛡️ Administrator
- Monitor overall platform health and statistics.
- Moderate users and force-close outdated job listings.
- Manually trigger backend asynchronous jobs (like email alerts) for testing.

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL Server (if running in production mode)

### 1. Clone the repository
```bash
git clone https://github.com/abhishek-2247/spring-boot-job-portal.git
cd spring-boot-job-portal
```

### 2. Configure the Database
By default, the application runs on an **H2 In-Memory Database** (if uncommented in the properties). To run against a persistent MySQL database:
1. Open `src/main/resources/application.properties`
2. Create a database named `jobportal` in your MySQL instance.
3. Update `spring.datasource.username` and `spring.datasource.password` to match your local MySQL credentials.

### 3. Build & Run
```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

The application will start on port `8090`. 
Access it at: [http://localhost:8090](http://localhost:8090)

---

<div align="center">
  <p>Built with ❤️ as an Enterprise Application Development Project.</p>
</div>
