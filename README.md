# Currency Exchange App

A Kotlin-based Android application for exchanging currencies, designed to demonstrate proficiency with modern Android development tools and architecture. The app dynamically updates currency exchange rates, allows users to perform currency exchange operations, and handles edge cases such as insufficient funds or invalid inputs.

---

## **Features**
- **Dynamic Currency Exchange Rates:**
  - Fetches live exchange rates from a mock API and updates them every 5 seconds.
  - Displays available currencies and their respective exchange rates.
  
- **Currency Conversion:**
  - Allows the user to select a currency to sell and a currency to buy.
  - Calculates and displays the resulting amount and applicable commission fee.

- **Error Handling:**
  - Shows an error dialog in case of API failures or invalid input.
  - Ensures no exchange is performed if the user has insufficient funds.

- **State Management:**
  - Uses `StateFlow` to manage UI state and ensure a responsive interface.

---

## **Architecture**
The app follows a modern **MVVM** architecture pattern with the following components:

1. **ViewModel:**
   - Handles business logic and state management.
   - Manages the lifecycle of coroutines for data fetching and user interaction.
   
2. **Repository:**
   - Abstracts data fetching and maps API responses to domain models.

3. **UseCase:**
   - Encapsulates application-specific business rules (e.g., fetching exchange rates).

4. **UI Layer:**
   - Built with **Jetpack Compose** for a modern, declarative UI.
   - Includes components like `Dialog` for success/error feedback.

---

## **Tech Stack**
- **Programming Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM
- **Coroutines & Flow:** For asynchronous operations and state management.
- **Dependency Injection:** Dagger Hilt
- **Unit Testing:**
  - Mocking with MockK
  - Coroutine-based testing using `kotlinx.coroutines.test`

---
