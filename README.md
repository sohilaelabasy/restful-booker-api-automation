# 🚀 Restful Booker API Automation Framework

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![TestNG](https://img.shields.io/badge/TestNG-Framework-green)
![Rest Assured](https://img.shields.io/badge/RestAssured-API%20Testing-brightgreen)
![Allure](https://img.shields.io/badge/Allure-Reporting-blue)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI/CD-success)

## 📖 Overview

This project is an API Test Automation Framework built for the **Restful Booker API** using modern automation testing practices.

### 🛠️ Tech Stack

* ☕ Java 21
* 🌐 Rest Assured
* 🧪 TestNG
* 📦 Maven
* 📊 Allure Reports
* ⚙️ GitHub Actions

The framework validates core booking functionalities including authentication, booking management, filtering, and health check endpoints.

---

## ✨ Features

✅ API Test Automation using Rest Assured

✅ Smoke & Regression Test Suites

✅ Request & Response Validation

✅ POJO Serialization & Deserialization

✅ Allure Reporting Integration

✅ GitHub Actions CI/CD Pipeline

✅ Automatic Allure Report Publishing via GitHub Pages

---

## 🎯 Test Coverage

### 🔐 Authentication

* Valid Credentials
* Invalid Credentials
* Empty Credentials
* Security & Validation Scenarios

### 📅 Booking Management

* Create Booking
* Get Booking
* Get All Bookings
* Update Booking
* Partial Update Booking
* Delete Booking

### 🔍 Search & Filtering

* Filter by First Name
* Filter by Last Name
* Filter by Check-in Date
* Filter by Check-out Date

### ❤️ Health Check

* API Availability Validation

---

## 🧪 Test Suites

### 🚦 Smoke Suite

Critical business flow validation:

* Authentication
* Health Check
* Create Booking
* Get Booking
* Update Booking
* Partial Update Booking
* Delete Booking

### 🔄 Regression Suite

Complete validation of all positive and negative scenarios.

---

## 📊 Reporting

All test executions generate **Allure Reports** automatically.

### 🔗 Latest Allure Report

👉 https://sohilaelabasy.github.io/restful-booker-api-automation/

---

## ⚙️ Continuous Integration

GitHub Actions pipeline automatically:

```text
Push to Main
        ↓
 Run Smoke Tests
        ↓
 Generate Allure Report
        ↓
 Publish to GitHub Pages
```

Triggered on every push to the `main` branch.

---

## 💻 Run Tests Locally

### 🚦 Smoke Suite

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-suites/smoke-suite.xml
```

### 🔄 Regression Suite

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-suites/regression-suite.xml
```

---

## 📂 Project Structure

```text
src
├── main
│   ├── models
│   ├── requests
│   └── utils
│
├── test
│   ├── tests
│   └── resources
│       └── testng-suites

.github
└── workflows

allure-results
```

---

## 👩‍💻 Author

**Sohila Elabasy**

QA Engineer | Manual & Automation Testing

🔗 GitHub: https://github.com/sohilaelabasy
