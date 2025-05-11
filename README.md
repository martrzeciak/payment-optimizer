# 💳🛒 Payment Optimization App

## 📝 Overview

This Java application calculates the optimal way to pay for a set of supermarket orders using a combination of loyalty points and traditional payment methods (such as bank cards), maximizing the total customer discount while obeying all business rules and payment method constraints.

## 🚀 Features

- 📥 Reads orders and payment methods from JSON files.
- 📋 Implements all business rules described in the specification.
- 📊 Outputs a summary of spent amounts per payment method.
- 🧪 Well-structured, testable, and documented Java codebase.

## 📜 Business Rules (Summary)

1. 🏦 **Promotional Discounts:**
   Orders can be assigned to specific bank cards for a discount, but only if the entire order is paid with that card.

2. 🎁 **Loyalty Points:**
    - Full payment with points applies the points discount.
    - Partial payment (≥10% of order value) with points grants a 10% discount on the whole order; the rest can be paid with any card.
    - If both card and points are possible, the algorithm chooses the combination that maximizes the total discount.

3. 🛡️ **Fallback:**
   If no discounts are applicable, the order is paid with any available card.

## 🛠️ Tech Stack

- ☕ **Java 21**
- 🛠️ **Gradle** (build tool)
- 🗃️ **Jackson** (JSON parsing)
- 🧪 **JUnit 5** (unit testing)

## ⚙️ How to Build & Run (tested on Windows 11 with included wrapper)

### 📋 Prerequisites

- Java 21
- Gradle (wrapper included, or install system-wide)

### 🏗️ Build (CMD)
```cmd
gradlew clean build
```

### 🧪 Running Tests (CMD)
```cmd
gradlew test
```

### ▶️ Run .jar
```cmd
 java -jar build/libs/payment-optimizer-1.0-SNAPSHOT.jar data/orders.json data/paymentmethods.json
```

## 🗂️ Project Structure

- `src/main/java/com/domain/model/` - Data models (`Order`, `PaymentMethod`) 🗃️
- `src/main/java/com/domain/service/` - Core logic (`PaymentOptimizer`, `FileLoader`, `ResultPrinter`) ⚙️
- `src/main/java/com/domain/Main.java` - Application entry point 🚪
- `src/test/java/com/domain/service/` - Unit tests 🧪
