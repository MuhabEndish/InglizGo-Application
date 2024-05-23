
# InglizGo_Application

## Overview

**Inglizgo_v3** is a JavaFX-based application designed to manage quizzes, user performance reviews, and user authentication functionalities such as login, signup, and password recovery. The project is built using Maven for dependency management and project configuration.

## Project Structure

- **.gitignore**: Lists files and directories to be ignored by Git.
- **mvnw** and **mvnw.cmd**: Maven wrapper scripts.
- **pom.xml**: Maven configuration file.
- **.idea/**: IntelliJ IDEA configuration files.
- **src/main/java/**: Java source files.
- **src/main/resources/**: Resource files like FXML and CSS.
- **target/**: Compiled classes and build artifacts.
- **inglizgo_app.sql**: SQL script for setting up the database.

## Key Components

### Java Classes

- **Inglizgo3Application.java**: Main class to launch the application.
- **AlertMessage.java**: Handles alert messages.
- **Login_SignUp_ForgotPasswordController.java**: Manages user authentication.
- **MainFormController.java**: Controls the main form.
- **PerformanceData.java**: Represents performance data.
- **PerformanceReviewController.java**: Manages performance reviews.
- **Question.java**: Represents quiz questions.
- **QuizManager.java**: Manages quiz operations.
- **QuizScreenController.java**: Controls the quiz interface.

### Resource Files

- **FXML Files**: Define the UI layout and UI styling.
  - `Login_SignUp_ForotPassword.fxml`
  - `mainForm.fxml`
  - `PerformanceReview.fxml`
  - `quiz_screen.fxml`

## Prerequisites

- Java Development Kit (JDK) 11 or higher.
- Apache Maven 3.6.0 or higher.
- MySQL or any compatible SQL database.

## Setup and Running the Application

### 1. Clone the repository
\`\`\`sh
git clone <repository_url>
\`\`\`

### 2. Navigate to the project directory
\`\`\`sh
cd Inglizgo_v3
\`\`\`

### 3. Build the project using Maven
\`\`\`sh
./mvnw clean install
\`\`\`

### 4. Set up the Database
1. Ensure MySQL is installed and running.
2. Create a new database:
    \`\`\`sql
    CREATE DATABASE inglizgo_db;
    \`\`\`
3. Import the provided database schema:
    \`\`\`sh
    mysql -u username -p inglizgo_db < path/to/inglizgo_app.sql
    \`\`\`
4. Update the database configuration in the application properties file (\`src/main/resources/application.properties\`):
    \`\`\`properties
    spring.datasource.url=jdbc:mysql://localhost:3306/inglizgo_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    \`\`\`

### 5. Run the application
\`\`\`sh
./mvnw javafx:run
\`\`\`

## Contributing

1. Fork the repository.
2. Create a new branch (\`git checkout -b feature-branch\`).
3. Commit your changes (\`git commit -am 'Add new feature'\`).
4. Push to the branch (\`git push origin feature-branch\`).
5. Create a new Pull Request.