# 🚆 Train Booking System (Core Java)

A console-based **Train Booking System** built using **Core Java** and **Jackson** for JSON-based persistence.  
This project simulates basic IRCTC-like functionality such as user authentication, train search, seat booking, and ticket cancellation.

> ⚠️ This project does **NOT** use Spring or Spring Boot.  
> The word **Spring** appears only in the package name, not as a framework.

---

## 📌 Features

- User Signup & Login with password hashing  
- Search trains between source and destination  
- Multiple trains with multiple routes  
- Seat availability and booking  
- Ticket cancellation  
- JSON-based local database (no SQL)  
- Travel date support using `LocalDate`  
- Secure password hashing using bcrypt  

---

## 🛠️ Tech Stack

- **Java** – Core application logic  
- **Jackson** – JSON serialization/deserialization  
- **Maven** – Dependency management  
- **bcrypt** – Password hashing  
- **JSON** – Local file-based database  

---

## 📂 Project Structure


IRCTC/
│
├── src/main/java/
│   └── org/Spring/
│       ├── App.java                 # Main entry point
│       ├── entities/
│       │   ├── User.java
│       │   ├── Train.java
│       │   └── Ticket.java
│       ├── service/
│       │   ├── UserBookingService.java
│       │   └── TrainService.java
│       └── utils/
│           └── UserServiceUtil.java
│
├── localDb/
│   ├── users.json                   # User database
│   └── trains.json                  # Train database
│
├── pom.xml
└── README.md


---

## ▶️ How to Run the Project

### 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/IRCTC.git
cd IRCTC

2️⃣ Build the project
mvn clean install

3️⃣ Run the application
mvn exec:java -Dexec.mainClass="org.Spring.App"

🧪 Sample Console Flow
🚆 Train Booking System

1. Sign up
2. Login
3. Fetch Bookings
4. Search Trains
5. Book a Seat
6. Cancel Booking
7. Exit



