package com.studymate.persistence;

import com.studymate.model.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence implementation using H2 Database (SQL).
 * 
 * Schema:
 * - COURSES: ID, NAME, INSTRUCTOR, SEMESTER, CREDITS, DESCRIPTION
 * - ASSIGNMENTS: ID, COURSE_ID, TITLE, DESCRIPTION, DUE_DATE, PRIORITY, STATUS
 * - NOTES: ID, COURSE_ID, TITLE, CONTENT, CREATED_ON
 * - TESTS: ID, COURSE_ID, NAME, DATE, MAX_SCORE, SCORE
 * - STUDY_HABITS: ID, NAME, DESCRIPTION, WEEKLY_TARGET
 * - HABIT_LOGS: ID, HABIT_ID, DATE, AMOUNT, NOTE
 */
public class SqlAppStateRepository implements AppStateRepository {

    private final String dbUrl;
    private final String user = "sa";
    private final String password = "";

    public SqlAppStateRepository(String dbUrl) {
        this.dbUrl = dbUrl;
        initializeSchema();
    }

    private void initializeSchema() {
        String[] createTables = {
                "CREATE TABLE IF NOT EXISTS COURSES (ID INT PRIMARY KEY, NAME VARCHAR(255), INSTRUCTOR VARCHAR(255), SEMESTER VARCHAR(50), CREDITS INT, DESCRIPTION VARCHAR(2000))",
                "CREATE TABLE IF NOT EXISTS ASSIGNMENTS (ID INT PRIMARY KEY, COURSE_ID INT, TITLE VARCHAR(255), DESCRIPTION VARCHAR(2000), DUE_DATE DATE, PRIORITY INT, STATUS VARCHAR(50))",
                "CREATE TABLE IF NOT EXISTS NOTES (ID INT PRIMARY KEY, COURSE_ID INT, TITLE VARCHAR(255), CONTENT VARCHAR(2000), CREATED_ON DATE)",
                "CREATE TABLE IF NOT EXISTS TESTS (ID INT PRIMARY KEY, COURSE_ID INT, NAME VARCHAR(255), TEST_DATE DATE, MAX_SCORE DOUBLE, SCORE DOUBLE)",
                "CREATE TABLE IF NOT EXISTS STUDY_HABITS (ID INT PRIMARY KEY, NAME VARCHAR(255), DESCRIPTION VARCHAR(2000), WEEKLY_TARGET INT)",
                "CREATE TABLE IF NOT EXISTS HABIT_LOGS (ID INT PRIMARY KEY, HABIT_ID INT, LOG_DATE DATE, AMOUNT INT, NOTE VARCHAR(2000))"
        };

        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
                Statement stmt = conn.createStatement()) {
            for (String sql : createTables) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(AppState state) throws IOException {
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                // Wipe existing data (Full state overwrite strategy)
                stmt.execute("DELETE FROM HABIT_LOGS");
                stmt.execute("DELETE FROM STUDY_HABITS");
                stmt.execute("DELETE FROM TESTS");
                stmt.execute("DELETE FROM NOTES");
                stmt.execute("DELETE FROM ASSIGNMENTS");
                stmt.execute("DELETE FROM COURSES");

                insertCourses(conn, state.getCourses());
                insertAssignments(conn, state.getAssignments());
                insertNotes(conn, state.getNotes());
                insertTests(conn, state.getTests());
                insertHabits(conn, state.getHabits());
                insertHabitLogs(conn, state.getHabitLogs());

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IOException("Failed to save state to SQL DB", e);
        }
    }

    private void insertCourses(Connection conn, List<Course> items) throws SQLException {
        String sql = "INSERT INTO COURSES (ID, NAME, INSTRUCTOR, SEMESTER, CREDITS, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Course i : items) {
                ps.setInt(1, i.getCourseId());
                ps.setString(2, i.getCourseName());
                ps.setString(3, i.getInstructorName());
                ps.setString(4, i.getSemester());
                ps.setInt(5, i.getCreditHours());
                ps.setString(6, i.getDescription());
                ps.executeUpdate();
            }
        }
    }

    private void insertAssignments(Connection conn, List<Assignment> items) throws SQLException {
        String sql = "INSERT INTO ASSIGNMENTS (ID, COURSE_ID, TITLE, DESCRIPTION, DUE_DATE, PRIORITY, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Assignment i : items) {
                ps.setInt(1, i.getAssignmentId());
                ps.setInt(2, i.getCourseId());
                ps.setString(3, i.getTitle());
                ps.setString(4, i.getDescription());
                ps.setDate(5, Date.valueOf(i.getDueDate()));
                ps.setInt(6, i.getPriority());
                ps.setString(7, i.getStatus());
                ps.executeUpdate();
            }
        }
    }

    private void insertNotes(Connection conn, List<Note> items) throws SQLException {
        String sql = "INSERT INTO NOTES (ID, COURSE_ID, TITLE, CONTENT, CREATED_ON) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Note i : items) {
                ps.setInt(1, i.getNoteId());
                ps.setInt(2, i.getCourseId());
                ps.setString(3, i.getTitle());
                ps.setString(4, i.getContent());
                ps.setDate(5, Date.valueOf(i.getCreatedOn()));
                ps.executeUpdate();
            }
        }
    }

    private void insertTests(Connection conn, List<Test> items) throws SQLException {
        String sql = "INSERT INTO TESTS (ID, COURSE_ID, NAME, TEST_DATE, MAX_SCORE, SCORE) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Test i : items) {
                ps.setInt(1, i.getTestId());
                ps.setInt(2, i.getCourseId());
                ps.setString(3, i.getName());
                ps.setDate(4, Date.valueOf(i.getDate()));
                ps.setDouble(5, i.getMaxScore());
                ps.setDouble(6, i.getScore());
                ps.executeUpdate();
            }
        }
    }

    private void insertHabits(Connection conn, List<StudyHabit> items) throws SQLException {
        String sql = "INSERT INTO STUDY_HABITS (ID, NAME, DESCRIPTION, WEEKLY_TARGET) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (StudyHabit i : items) {
                ps.setInt(1, i.getHabitId());
                ps.setString(2, i.getName());
                ps.setString(3, i.getDescription());
                ps.setInt(4, i.getWeeklyTarget());
                ps.executeUpdate();
            }
        }
    }

    private void insertHabitLogs(Connection conn, List<HabitLog> items) throws SQLException {
        String sql = "INSERT INTO HABIT_LOGS (ID, HABIT_ID, LOG_DATE, AMOUNT, NOTE) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (HabitLog i : items) {
                ps.setInt(1, i.getLogId());
                ps.setInt(2, i.getHabitId());
                ps.setDate(3, Date.valueOf(i.getDate()));
                ps.setInt(4, i.getAmount());
                ps.setString(5, i.getNote());
                ps.executeUpdate();
            }
        }
    }

    @Override
    public AppState load() throws IOException {
        AppState state = new AppState();
        try (Connection conn = DriverManager.getConnection(dbUrl, user, password)) {
            state.setCourses(loadCourses(conn));
            state.setAssignments(loadAssignments(conn));
            state.setNotes(loadNotes(conn));
            state.setTests(loadTests(conn));
            state.setHabits(loadHabits(conn));
            state.setHabitLogs(loadHabitLogs(conn));
        } catch (SQLException e) {
            throw new IOException("Failed to load state from SQL DB", e);
        }
        return state;
    }

    private List<Course> loadCourses(Connection conn) throws SQLException {
        List<Course> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM COURSES")) {
            while (rs.next()) {
                list.add(new Course(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getString("INSTRUCTOR"),
                        rs.getString("SEMESTER"),
                        rs.getInt("CREDITS"),
                        rs.getString("DESCRIPTION")));
            }
        }
        return list;
    }

    private List<Assignment> loadAssignments(Connection conn) throws SQLException {
        List<Assignment> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM ASSIGNMENTS")) {
            while (rs.next()) {
                list.add(new Assignment(
                        rs.getInt("ID"),
                        rs.getInt("COURSE_ID"),
                        rs.getString("TITLE"),
                        rs.getString("DESCRIPTION"),
                        rs.getDate("DUE_DATE").toLocalDate(),
                        rs.getInt("PRIORITY"),
                        rs.getString("STATUS")));
            }
        }
        return list;
    }

    private List<Note> loadNotes(Connection conn) throws SQLException {
        List<Note> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM NOTES")) {
            while (rs.next()) {
                list.add(new Note(
                        rs.getInt("ID"),
                        rs.getInt("COURSE_ID"),
                        rs.getString("TITLE"),
                        rs.getString("CONTENT"),
                        rs.getDate("CREATED_ON").toLocalDate()));
            }
        }
        return list;
    }

    private List<Test> loadTests(Connection conn) throws SQLException {
        List<Test> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM TESTS")) {
            while (rs.next()) {
                list.add(new Test(
                        rs.getInt("ID"),
                        rs.getInt("COURSE_ID"),
                        rs.getString("NAME"),
                        rs.getDate("TEST_DATE").toLocalDate(),
                        rs.getDouble("MAX_SCORE"),
                        rs.getDouble("SCORE")));
            }
        }
        return list;
    }

    private List<StudyHabit> loadHabits(Connection conn) throws SQLException {
        List<StudyHabit> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM STUDY_HABITS")) {
            while (rs.next()) {
                list.add(new StudyHabit(
                        rs.getInt("ID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION"),
                        rs.getInt("WEEKLY_TARGET")));
            }
        }
        return list;
    }

    private List<HabitLog> loadHabitLogs(Connection conn) throws SQLException {
        List<HabitLog> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM HABIT_LOGS")) {
            while (rs.next()) {
                list.add(new HabitLog(
                        rs.getInt("ID"),
                        rs.getInt("HABIT_ID"),
                        rs.getDate("LOG_DATE").toLocalDate(),
                        rs.getInt("AMOUNT"),
                        rs.getString("NOTE")));
            }
        }
        return list;
    }
}
