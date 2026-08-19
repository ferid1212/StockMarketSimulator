# 🛒 Stock System - Inventory & E-Commerce Management Platform

This project is a **Spring Boot** full-stack application designed for inventory management and providing an intuitive e-commerce experience for users. The system features role-based access control, product filtering, containerized deployment, and automated background cleanup processes.

---

## 🚀 Features

### 🔐 Security & Role Management
* **Role-Based Access Control (RBAC):** Distinct functional access for Admins and Users (`ROLE_ADMIN`, `ROLE_USER`).
* **Spring Security:** Robust authentication and authorization setup.

### 👨‍💼 Admin Panel
* **Product Management:** Complete CRUD functionality to add, update, or remove products.
* **Stock & Inventory Control:** Real-time updates for product stock levels and pricing.
* **User Management:** View registered users and manage user roles/access.

### 👤 User Panel
* **Catalog & Filtering:** Search and filter products by name and category.
* **Cart & Wallet:** Add items to cart, top up virtual balance, and complete checkout.
* **Favorites:** Save preferred products to a personalized favorites list.

### ⏱️ Automated Background Tasks (Scheduled Tasks)
* **Basket Cleanup Service:** Powered by `@Scheduled`, items left in user carts for over 24 hours are automatically removed to free up reserved inventory.

---

## 🛠️ Tech Stack

* **Backend:** Java 17+, Spring Boot, Spring Security, Spring Data JPA
* **Frontend:** HTML5, CSS3, JavaScript, Thymeleaf
* **Database:** PostgreSQL
* **DevOps & Containerization:** Docker, Docker Compose
* **Tools & Build:** Gradle, MapStruct, Lombok

---

## ⚙️ Setup & Installation

### Option 1: Run with Docker Compose (Recommended)

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/ferid1212/StockMarketSimulator.git](https://github.com/ferid1212/StockMarketSimulator.git)
   cd StockMarketSimulator