# JavaFX Smart Calculator

A high-precision, state-aware calculator built with JavaFX. This project demonstrates clean architecture by separating math logic, error handling, and UI styling.

## ✨ Key Features
* **High Precision:** Uses `BigDecimal` for all calculations to avoid floating-point errors.
* **UI State Management:** Automatically disables operation buttons during error states (e.g., Division by Zero) to prevent invalid inputs.
* **CSS Skinning:** Full separation of UI design from Java logic using an external stylesheet.
* **Enum-Driven Engine:** Uses Enums for math operations and error messaging for maximum code readability.

## 🛠 Technical Stack
* **Java 17+**
* **JavaFX 21**
* **Maven** (Dependency Management)

## 🚀 How to Run
1. Clone the repository: `git clone https://github.com/axiomdevv/JavaFX-Smart-Calculator.git`
2. Open in IntelliJ/Eclipse as a Maven project.
3. Run `Launcher.java`.
