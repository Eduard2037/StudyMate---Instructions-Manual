package com.studymate.persistence;

import com.studymate.model.Assignment;
import com.studymate.model.Course;
import com.studymate.model.HabitLog;
import com.studymate.model.Note;
import com.studymate.model.StudyHabit;
import com.studymate.model.Test;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON-based persistence using the org.json library (Lab 6).
 */
public class JsonAppStateRepository implements AppStateRepository {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final Path file;

    public JsonAppStateRepository(String filePath) {
        this.file = Paths.get(filePath);
    }

    @Override
    public void save(AppState state) throws IOException {
        JSONObject root = new JSONObject();

        // Courses
        JSONArray coursesArray = new JSONArray();
        for (Course c : state.getCourses()) {
            JSONObject o = new JSONObject();
            o.put("id", c.getCourseId());
            o.put("name", c.getCourseName());
            o.put("instructor", c.getInstructorName());
            o.put("semester", c.getSemester());
            o.put("creditHours", c.getCreditHours());
            o.put("description", c.getDescription());
            coursesArray.put(o);
        }
        root.put("courses", coursesArray);

        // Assignments
        JSONArray assignmentsArray = new JSONArray();
        for (Assignment a : state.getAssignments()) {
            JSONObject o = new JSONObject();
            o.put("id", a.getAssignmentId());
            o.put("courseId", a.getCourseId());
            o.put("title", a.getTitle());
            o.put("description", a.getDescription());
            o.put("dueDate", a.getDueDate().format(DATE_FORMAT));
            o.put("priority", a.getPriority());
            o.put("status", a.getStatus());
            assignmentsArray.put(o);
        }
        root.put("assignments", assignmentsArray);

        // Notes
        JSONArray notesArray = new JSONArray();
        for (Note n : state.getNotes()) {
            JSONObject o = new JSONObject();
            o.put("id", n.getNoteId());
            o.put("courseId", n.getCourseId());
            o.put("title", n.getTitle());
            o.put("content", n.getContent());
            o.put("createdOn", n.getCreatedOn().format(DATE_FORMAT));
            notesArray.put(o);
        }
        root.put("notes", notesArray);

        // Tests
        JSONArray testsArray = new JSONArray();
        for (Test t : state.getTests()) {
            JSONObject o = new JSONObject();
            o.put("id", t.getTestId());
            o.put("courseId", t.getCourseId());
            o.put("name", t.getName());
            o.put("date", t.getDate().format(DATE_FORMAT));
            o.put("maxScore", t.getMaxScore());
            o.put("score", t.getScore());
            testsArray.put(o);
        }
        root.put("tests", testsArray);

        // Habits
        JSONArray habitsArray = new JSONArray();
        for (StudyHabit h : state.getHabits()) {
            JSONObject o = new JSONObject();
            o.put("id", h.getHabitId());
            o.put("name", h.getName());
            o.put("description", h.getDescription());
            o.put("weeklyTarget", h.getWeeklyTarget());
            habitsArray.put(o);
        }
        root.put("habits", habitsArray);

        // Habit logs
        JSONArray logsArray = new JSONArray();
        for (HabitLog log : state.getHabitLogs()) {
            JSONObject o = new JSONObject();
            o.put("id", log.getLogId());
            o.put("habitId", log.getHabitId());
            o.put("date", log.getDate().format(DATE_FORMAT));
            o.put("amount", log.getAmount());
            o.put("note", log.getNote());
            logsArray.put(o);
        }
        root.put("habitLogs", logsArray);

        // Save to disk
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (Writer writer = Files.newBufferedWriter(file)) {
            writer.write(root.toString(2)); // pretty-printed JSON
        }
    }

    @Override
    public AppState load() throws IOException {
        if (!Files.exists(file)) {
            return new AppState();
        }

        String json = Files.readString(file);
        JSONObject root = new JSONObject(json);
        AppState state = new AppState();

        // Courses
        List<Course> courses = new ArrayList<>();
        JSONArray coursesArray = root.optJSONArray("courses");
        if (coursesArray != null) {
            for (int i = 0; i < coursesArray.length(); i++) {
                JSONObject o = coursesArray.getJSONObject(i);
                Course c = new Course(
                        o.getInt("id"),
                        o.getString("name"),
                        o.getString("instructor"),
                        o.getString("semester"),
                        o.getInt("creditHours"),
                        o.optString("description", "")
                );
                courses.add(c);
            }
        }
        state.setCourses(courses);

        // Assignments
        List<Assignment> assignments = new ArrayList<>();
        JSONArray assignmentsArray = root.optJSONArray("assignments");
        if (assignmentsArray != null) {
            for (int i = 0; i < assignmentsArray.length(); i++) {
                JSONObject o = assignmentsArray.getJSONObject(i);
                Assignment a = new Assignment(
                        o.getInt("id"),
                        o.getInt("courseId"),
                        o.getString("title"),
                        o.getString("description"),
                        java.time.LocalDate.parse(o.getString("dueDate"), DATE_FORMAT),
                        o.getInt("priority"),
                        o.getString("status")
                );
                assignments.add(a);
            }
        }
        state.setAssignments(assignments);

        // Notes
        List<Note> notes = new ArrayList<>();
        JSONArray notesArray = root.optJSONArray("notes");
        if (notesArray != null) {
            for (int i = 0; i < notesArray.length(); i++) {
                JSONObject o = notesArray.getJSONObject(i);
                Note n = new Note(
                        o.getInt("id"),
                        o.getInt("courseId"),
                        o.getString("title"),
                        o.getString("content"),
                        java.time.LocalDate.parse(o.getString("createdOn"), DATE_FORMAT)
                );
                notes.add(n);
            }
        }
        state.setNotes(notes);

        // Tests
        List<Test> tests = new ArrayList<>();
        JSONArray testsArray = root.optJSONArray("tests");
        if (testsArray != null) {
            for (int i = 0; i < testsArray.length(); i++) {
                JSONObject o = testsArray.getJSONObject(i);
                Test t = new Test(
                        o.getInt("id"),
                        o.getInt("courseId"),
                        o.getString("name"),
                        java.time.LocalDate.parse(o.getString("date"), DATE_FORMAT),
                        o.getDouble("maxScore"),
                        o.getDouble("score")
                );
                tests.add(t);
            }
        }
        state.setTests(tests);

        // Habits
        List<StudyHabit> habits = new ArrayList<>();
        JSONArray habitsArray = root.optJSONArray("habits");
        if (habitsArray != null) {
            for (int i = 0; i < habitsArray.length(); i++) {
                JSONObject o = habitsArray.getJSONObject(i);
                StudyHabit h = new StudyHabit(
                        o.getInt("id"),
                        o.getString("name"),
                        o.getString("description"),
                        o.getInt("weeklyTarget")
                );
                habits.add(h);
            }
        }
        state.setHabits(habits);

        // Habit logs
        List<HabitLog> logs = new ArrayList<>();
        JSONArray logsArray = root.optJSONArray("habitLogs");
        if (logsArray != null) {
            for (int i = 0; i < logsArray.length(); i++) {
                JSONObject o = logsArray.getJSONObject(i);
                HabitLog log = new HabitLog(
                        o.getInt("id"),
                        o.getInt("habitId"),
                        java.time.LocalDate.parse(o.getString("date"), DATE_FORMAT),
                        o.getInt("amount"),
                        o.optString("note", "")
                );
                logs.add(log);
            }
        }
        state.setHabitLogs(logs);

        return state;
    }
}
