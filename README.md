
# 🌍 Journey Pro - Trip Management System

A comprehensive travel and trip management platform built using **HTML**, **CSS**, **JavaScript**, and **Spring Boot**. The application provides features for both **administrators** and **users** to manage and book various travel services efficiently.

---

## 🚀 Features

### 🔑 User Roles and Permissions

#### 👤 Customer

- **Features**:
  - User authentication (signup, login, logout)
  - Browse and book trips, hotels, vehicles, and buses
  - Manage bookings and view history
  - Real-time availability checking
  - Secure payment processing
  - Profile management

#### 👨‍💼 Admin

- **Features**:
  - Comprehensive dashboard with analytics
  - Manage users, trips, bookings, and resources
  - Hotel and vehicle fleet management
  - Bus schedule management
  - Generate reports and analytics
  - System configuration

---

## 🛠️ Technologies Used

| **Frontend**          | **Backend**     | **Database** |
|-----------------------|------------------|--------------|
| HTML, CSS, JavaScript | Spring Boot      | MySQL        |

### Additional Highlights:
- **Authentication**: JWT-based authentication
- **Security**: Spring Security
- **API Documentation**: Swagger UI
- **Persistence**: JPA / Hibernate
- **Real-time Updates**: WebSocket integration

---

## 📷 Screenshots

### 🏠 Dashboard
![Admin Dashboard](screenshots/screenshot-dashboard.png)

### 🏨 Hotel Management
![Hotel Management](screenshots/screenshot-hotels.png)

### 🚗 Vehicle Management
![Vehicle Management](screenshots/screenshot-vehicles.png)

### 🚌 Bus Management
![Bus Management](screenshots/screenshot-buses.png)

> 💡 _Make sure the `screenshots/` folder contains the image files above, or adjust the paths as needed._

---

## 🎥 Demo Video

📺 Watch the complete walkthrough on [YouTube](https://youtu.be/your-video-id)  
> _(Replace `your-video-id` with the actual YouTube video ID)_

---

## 📦 Installation

### Prerequisites
- [JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [MySQL](https://www.mysql.com/)
- [Maven](https://maven.apache.org/)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Chathura0607/Journey-Pro.git
   cd Journey-Pro
   ```

2. **Configure the database**
   - Create a MySQL database named `journeypro`
   - Update `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/journeypro
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. **Build and run the application**
   ```bash
   mvn clean install
   java -jar target/journey-pro-0.0.1-SNAPSHOT.jar
   ```
---

## 🔧 Usage

### 👤 Customer
1. Register and log in
2. Browse available trips, hotels, vehicles, and buses
3. Make bookings and payments
4. Track booking status and history

### 👨‍💼 Admin
1. Log in to admin dashboard
2. Monitor system statistics
3. Manage resources (hotels, vehicles, buses)
4. Handle bookings and users
5. Generate reports and perform system configurations

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Create a new Pull Request

---

## 📬 Contact

📧 **Email**: [chathuhiru45@gmail.com](mailto:chathuhiru45@gmail.com)  
🐙 **GitHub**: [Chathura0607](https://github.com/Chathura0607)  
💼 **LinkedIn**: [Your LinkedIn](https://linkedin.com/in/your-profile)

> 🔁 Replace `"your-profile"` with your actual LinkedIn profile URL.

---
