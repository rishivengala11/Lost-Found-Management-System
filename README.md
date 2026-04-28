# Lost & Found Management System

## Overview
The **Lost & Found Management System** is a centralized web-based application designed to help users report lost items and claim found items efficiently. Developed with the Gokaraju Rangaraju Institute of Engineering and Technology community in mind, it bridges the gap between individuals who have lost personal belongings and those who have found them.

## Features

### User Features
- **Authentication:** Secure user registration, login, and email verification.
- **Report Items:** Easily submit detailed reports for items you have lost or found (category, description, date, location).
- **My Items Dashboard:** Dedicated dashboard to manage and track the real-time status of your reported items.
- **Smart Match Filtering:** A built-in scoring system algorithm automatically pairs lost and found items. The "Matches" page intelligently filters and displays only items with strong match scores (score of 4 or higher).
- **Claim/Return Requests:** Secure workflow to request a claim for a lost item or initiate a return for a found item.

### Administrator Features
- **Admin Authentication:** Consolidated, role-based authentication allowing administrators to securely log in.
- **Item Verification System:** All newly reported lost and found items go into a pending queue. Administrators must manually review and approve items before they become visible to the public.
- **Status Approvals:** Functionality to finalize or reject status changes (e.g., claiming an item) requested by standard users.
- **Automated Email Notifications:** The system automatically triggers email notifications to users informing them once their item has been successfully approved by an administrator.

### UI & Aesthetics
- **Modern UI Styling:** A clean, flat, and minimalist aesthetic overriding default Bootstrap with modern typography, dynamic hover interactions, and a polished teal-accented color palette.

## Technology Stack
- **Frontend:** HTML5, CSS3, JavaScript, HTML components.
- **Backend:** Java (Servlets, DAO Pattern, JDBC).
- **Database:** MySQL.
- **Mail:** JavaMail API (`javax.mail`) for verification links and notifications.
- **Server:** Apache Tomcat (bundled with XAMPP).

## Prerequisites
To run this project locally, ensure you have the following software installed:
1. **Java Development Kit (JDK):** Version 8 or higher.
2. **XAMPP:** Used for running the MySQL database and the Apache Tomcat server environment.
3. **Windows OS:** The project utilizes `.bat` files for compilation and deployment tasks.

## Installation & Setup

1. **Workspace Setup:**
   Extract or clone the project folder `Lost_Found` onto your local machine.

2. **Database Setup:**
   - Launch the XAMPP Control Panel and start the **MySQL** module.
   - To automatically configure the database schema and insert the initial admin account, double-click and run the `setup_db.bat` script provided in the root directory.
   - *(Alternatively, you can manually import the `db_schema.sql` file via phpMyAdmin or MySQL CLI).*

3. **Compiler Configuration:**
   - Open `compile.bat` and ensure the `CLASSPATH` variables point correctly to your local Tomcat `servlet-api.jar` (e.g., `C:\xampp\tomcat\lib\servlet-api.jar`).
   - Run `compile.bat` to compile all Java source files (Servlets, DAOs, and Models) into `WEB-INF/classes`.

4. **Deployment:**
   - Ensure the **Tomcat** module is running in XAMPP.
   - Run `deploy.bat`. This script will copy the updated project files directly into your XAMPP Tomcat `webapps/Lost_Found` directory, ready to serve.

## Usage
- Open your preferred web browser and navigate to the application URL:  
  `http://localhost:8080/Lost_Found/`

- **Initial Administrator Login:**
  - **Email:** `vengalarishi143@gmail.com`
  - **Password:** `admin123`
