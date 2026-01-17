package com.studymate.ui;

import com.studymate.model.Assignment;
import com.studymate.model.Course;
import com.studymate.persistence.UserCredentialsStore;
import com.studymate.service.AnalysisThread;
import com.studymate.service.StudyMateService;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * JavaFX UI for StudyMate, covering Lab 9 (GUI + validation)
 * and Lab 10 (Canvas demo). The dashboard after login now
 * has several additional options / scenes.
 */
public class StudyMateApplication extends Application {

    private StudyMateService service;
    private UserCredentialsStore credentialsStore;

    private Stage primaryStage;
    private Scene loginScene;
    private Scene registerScene;

    // Keep track of the currently logged in user so we can
    // show their email again when navigating back.
    private String currentUserEmail;

    // Canvas animation fields (Lab 10)
    private double ballX = 50;
    private double ballY = 50;
    private double ballDX = 3.5;
    private double ballDY = 2.4;
    private double ballRadius = 20;
    private AnimationTimer canvasTimer;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    @Override
    public void start(Stage primaryStage) {
        this.service = new StudyMateService();
        this.credentialsStore = new UserCredentialsStore("data/users.txt");
        this.primaryStage = primaryStage;

        this.loginScene = buildLoginScene();
        this.registerScene = buildRegisterScene();

        primaryStage.setTitle("StudyMate – Login");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);

        // Persist data on close
        primaryStage.setOnCloseRequest(event -> {
            try {
                service.saveAllData();    // CSV
                service.saveAsJson();     // JSON
                service.saveAsBinary();   // Object stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        primaryStage.show();
    }

    // ---------------- Register Scene ----------------

    private Scene buildRegisterScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label confirmLabel = new Label("Confirm Password:");
        PasswordField confirmField = new PasswordField();

        Label messageLabel = new Label();

        Button registerButton = new Button("Register");
        Button goToLoginButton = new Button("Already registered? Log in");

        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(passwordLabel, 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(confirmLabel, 0, 3);
        grid.add(confirmField, 1, 3);

        HBox buttons = new HBox(10, registerButton, goToLoginButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttons, 1, 4);

        VBox root = new VBox(10, grid, messageLabel);
        root.setPadding(new Insets(10));

        registerButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String name = nameField.getText().trim();
            String password = passwordField.getText();
            String confirm = confirmField.getText();

            String error = validateRegistration(email, name, password, confirm);
            if (error != null) {
                messageLabel.setText(error);
                return;
            }

            try {
                credentialsStore.registerUser(email, name, password);
                messageLabel.setText("Registration successful. You can now log in.");
            } catch (IllegalArgumentException ex) {
                messageLabel.setText(ex.getMessage());
            } catch (IOException ex) {
                messageLabel.setText("Failed to save credentials: " + ex.getMessage());
            }
        });

        goToLoginButton.setOnAction(e -> primaryStage.setScene(loginScene));

        return new Scene(root, 420, 260);
    }

    private String validateRegistration(String email, String name, String password, String confirm) {
        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            return "All fields are required.";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Invalid email format.";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        if (!password.equals(confirm)) {
            return "Passwords do not match.";
        }
        return null;
    }

    // ---------------- Login Scene ----------------

    private Scene buildLoginScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label messageLabel = new Label();

        Button loginButton = new Button("Log in");
        Button goToRegisterButton = new Button("Need an account? Register");

        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        HBox buttons = new HBox(10, loginButton, goToRegisterButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        grid.add(buttons, 1, 2);

        VBox root = new VBox(10, grid, messageLabel);
        root.setPadding(new Insets(10));

        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both email and password.");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                messageLabel.setText("Invalid email format.");
                return;
            }

            try {
                boolean ok = credentialsStore.authenticate(email, password);
                if (!ok) {
                    messageLabel.setText("Invalid credentials.");
                } else {
                    currentUserEmail = email;
                    showWelcomeScene(email);
                }
            } catch (IOException ex) {
                messageLabel.setText("Failed to read credentials: " + ex.getMessage());
            }
        });

        goToRegisterButton.setOnAction(e -> primaryStage.setScene(registerScene));

        return new Scene(root, 380, 200);
    }

    // ---------------- Welcome + Dashboard Scene ----------------

    private void showWelcomeScene(String email) {
        this.currentUserEmail = email;

        BorderPane root = new BorderPane();
        VBox centerBox = new VBox(12);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Welcome, " + email + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Display a simple "welcome" image if present on the classpath
        ImageView welcomeImage = loadWelcomeImage();

        Label infoLabel = new Label("You currently have "
                + service.getAssignments().size() + " assignments loaded.");

        // --- New dashboard options ---
        Button assignmentsButton = new Button("View upcoming assignments");
        Button coursesButton = new Button("View courses");
        Button statsButton = new Button("View statistics");

        Button canvasButton = new Button("Open Canvas Demo");
        Button analysisButton = new Button("Run threaded credit analysis");
        Button logoutButton = new Button("Log out");

        Label analysisResult = new Label("Analysis not started.");

        assignmentsButton.setOnAction(e -> showAssignmentsScene());
        coursesButton.setOnAction(e -> showCoursesScene());
        statsButton.setOnAction(e -> showStatsScene());

        canvasButton.setOnAction(e -> showCanvasScene());
        analysisButton.setOnAction(e -> runAnalysisInBackground(analysisResult));
        logoutButton.setOnAction(e -> {
            currentUserEmail = null;
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("StudyMate – Login");
        });

        centerBox.getChildren().add(welcomeLabel);
        if (welcomeImage != null) {
            centerBox.getChildren().add(welcomeImage);
        }
        centerBox.getChildren().add(infoLabel);

        VBox menuBox = new VBox(8,
                assignmentsButton,
                coursesButton,
                statsButton,
                canvasButton,
                analysisButton,
                analysisResult,
                logoutButton);
        menuBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(menuBox);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 520, 460);
        primaryStage.setTitle("StudyMate – Dashboard");
        primaryStage.setScene(scene);
    }

    private ImageView loadWelcomeImage() {
        try (InputStream is = getClass().getResourceAsStream("/com/studymate/images/welcome.png")) {
            if (is == null) {
                return null;
            }
            Image img = new Image(is);
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            // Image is optional – just ignore failures
            return null;
        }
    }

    // ----- New scenes: assignments, courses, statistics -----

    private void showAssignmentsScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label title = new Label("Upcoming assignments");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<Assignment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Assignment, String> colCourse = new TableColumn<>("Course");
        colCourse.setCellValueFactory(cell -> {
            Course c = service.getCourseById(cell.getValue().getCourseId());
            String name = (c != null) ? c.getCourseName() : "Unknown";
            return new SimpleStringProperty(name);
        });

        TableColumn<Assignment, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));

        TableColumn<Assignment, String> colDue = new TableColumn<>("Due date");
        colDue.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDueDate().toString()));

        TableColumn<Assignment, String> colPriority = new TableColumn<>("Priority");
        colPriority.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getPriority())));

        TableColumn<Assignment, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));

        table.getColumns().addAll(colCourse, colTitle, colDue, colPriority, colStatus);

        List<Assignment> upcoming = service.getUpcomingDeadlines();
        table.getItems().addAll(upcoming);

        Button backButton = new Button("Back to dashboard");
        Button markCompletedButton = new Button("Mark selected as completed");

        Label info = new Label("Double-click an assignment to see its course in the dashboard.");

        markCompletedButton.setOnAction(e -> {
            Assignment selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setStatus("Completed");
                table.refresh();
                try {
                    service.saveAllData();
                } catch (IOException ex) {
                    new Alert(Alert.AlertType.ERROR, "Failed to save assignments: " + ex.getMessage()).showAndWait();
                }
            }
        });

        backButton.setOnAction(e ->
                showWelcomeScene(currentUserEmail != null ? currentUserEmail : "student"));

        VBox topBox = new VBox(5, title, info);
        root.setTop(topBox);
        root.setCenter(table);

        HBox bottom = new HBox(10, markCompletedButton, backButton);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        Scene scene = new Scene(root, 720, 400);
        primaryStage.setTitle("StudyMate – Upcoming assignments");
        primaryStage.setScene(scene);
    }

    private void showCoursesScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label title = new Label("Your courses");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ListView<Course> listView = new ListView<>();
        listView.getItems().addAll(service.getCourses());

        TextArea details = new TextArea();
        details.setEditable(false);
        details.setWrapText(true);

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Course: ").append(newV.getCourseName()).append("\n");
                sb.append("Instructor: ").append(newV.getInstructorName()).append("\n");
                sb.append("Semester: ").append(newV.getSemester()).append("\n");
                sb.append("Credits: ").append(newV.getCreditHours()).append("\n");
                sb.append("Description: ").append(newV.getDescription()).append("\n");
                details.setText(sb.toString());
            } else {
                details.clear();
            }
        });

        HBox center = new HBox(10, listView, details);
        HBox.setHgrow(listView, Priority.NEVER);
        HBox.setHgrow(details, Priority.ALWAYS);

        Button backButton = new Button("Back to dashboard");
        backButton.setOnAction(e ->
                showWelcomeScene(currentUserEmail != null ? currentUserEmail : "student"));

        VBox topBox = new VBox(5, title);
        root.setTop(topBox);
        root.setCenter(center);
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER_RIGHT);
        BorderPane.setMargin(backButton, new Insets(10, 0, 0, 0));

        Scene scene = new Scene(root, 720, 400);
        primaryStage.setTitle("StudyMate – Courses");
        primaryStage.setScene(scene);
    }

    private void showStatsScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label title = new Label("Quick statistics");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        int totalAssignments = service.getAssignments().size();
        long completedAssignments = service.getAssignments().stream()
                .filter(Assignment::isCompleted)
                .count();
        long pendingAssignments = totalAssignments - completedAssignments;

        Map<Course, Long> completedByCourse = service.getCompletionCountsByCourse();

        VBox statsBox = new VBox(8);
        statsBox.getChildren().add(new Label("Total assignments: " + totalAssignments));
        statsBox.getChildren().add(new Label("Completed assignments: " + completedAssignments));
        statsBox.getChildren().add(new Label("Pending assignments: " + pendingAssignments));

        statsBox.getChildren().add(new Label("Completed assignments by course:"));
        for (Map.Entry<Course, Long> entry : completedByCourse.entrySet()) {
            Course c = entry.getKey();
            String name = (c != null) ? c.getCourseName() : "Unknown course";
            statsBox.getChildren().add(
                    new Label("  • " + name + ": " + entry.getValue())
            );
        }

        Button backButton = new Button("Back to dashboard");
        backButton.setOnAction(e ->
                showWelcomeScene(currentUserEmail != null ? currentUserEmail : "student"));

        root.setTop(title);
        root.setCenter(statsBox);
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER_RIGHT);
        BorderPane.setMargin(backButton, new Insets(10, 0, 0, 0));

        Scene scene = new Scene(root, 520, 360);
        primaryStage.setTitle("StudyMate – Statistics");
        primaryStage.setScene(scene);
    }

    // ---------------- Threads integration for dashboard ----------------

    private void runAnalysisInBackground(Label analysisResult) {
        analysisResult.setText("Running analysis on background thread...");
        AnalysisThread thread = service.startPendingCreditAnalysis();

        // Use another Java thread to wait for the analysis and then update the GUI.
        new Thread(() -> {
            try {
                thread.join();
                long result = thread.getTotalPendingCreditHours();
                Platform.runLater(() ->
                        analysisResult.setText("Total pending credit 'work units': " + result));
            } catch (InterruptedException e) {
                Platform.runLater(() -> analysisResult.setText("Analysis was interrupted."));
            }
        }, "AnalysisJoiner").start();
    }

    // ---------------- Canvas Scene (Lab 10) ----------------

    private void showCanvasScene() {
        BorderPane root = new BorderPane();
        Canvas canvas = new Canvas(600, 400);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Button backButton = new Button("Back to dashboard");
        backButton.setOnAction(e -> {
            if (canvasTimer != null) {
                canvasTimer.stop();
            }
            String email = currentUserEmail != null ? currentUserEmail : "student";
            showWelcomeScene(email);
        });

        root.setCenter(canvas);
        root.setBottom(backButton);
        BorderPane.setAlignment(backButton, Pos.CENTER);
        BorderPane.setMargin(backButton, new Insets(10));

        // Configure and start animation
        canvasTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateBallPosition(canvas.getWidth(), canvas.getHeight());
                drawFrame(gc, canvas.getWidth(), canvas.getHeight());
            }
        };
        canvasTimer.start();

        Scene scene = new Scene(root, 600, 450);
        primaryStage.setTitle("StudyMate – Canvas Demo");
        primaryStage.setScene(scene);
    }

    private void updateBallPosition(double width, double height) {
        ballX += ballDX;
        ballY += ballDY;

        if (ballX - ballRadius < 0 || ballX + ballRadius > width) {
            ballDX = -ballDX * 1.01; // bounce and slightly speed up
        }
        if (ballY - ballRadius < 0 || ballY + ballRadius > height) {
            ballDY = -ballDY * 1.01;
        }
    }

    private void drawFrame(GraphicsContext gc, double width, double height) {
        // Background
        gc.setFill(Color.DARKSLATEBLUE);
        gc.fillRect(0, 0, width, height);

        // Ball
        gc.setFill(Color.CORAL);
        gc.fillOval(ballX - ballRadius, ballY - ballRadius, ballRadius * 2, ballRadius * 2);

        // Some overlay text
        gc.setFill(Color.WHITE);
        gc.fillText("StudyMate Canvas Demo – bouncing ball", 10, 20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
