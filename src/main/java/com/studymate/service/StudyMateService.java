package com.studymate.service;

import com.studymate.exceptions.DuplicateIdException;
import com.studymate.exceptions.InvalidCourseException;
import com.studymate.model.Assignment;
import com.studymate.model.Course;
import com.studymate.model.HabitLog;
import com.studymate.model.Note;
import com.studymate.model.StudyHabit;
import com.studymate.model.Test;
import com.studymate.persistence.AppState;
import com.studymate.persistence.AppStateRepository;
import com.studymate.persistence.CsvPersistenceManager;
import com.studymate.persistence.DataInitializer;
import com.studymate.persistence.JsonAppStateRepository;
import com.studymate.persistence.ObjectStreamAppStateRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * Main application service managing collections, CRUD, business logic and
 * persistence orchestration (CSV, JSON and ObjectStreams).
 */
@Service
public class StudyMateService {

    // CSV persistence (Lab 5)
    private final CsvPersistenceManager<Course> courseCsv;
    private final CsvPersistenceManager<Assignment> assignmentCsv;

    // JSON & ObjectStream persistence (Lab 6)
    private final AppStateRepository jsonRepository;
    private final AppStateRepository objectRepository;

    // In-memory collections
    private List<Course> courses = new ArrayList<>();
    private List<Assignment> assignments = new ArrayList<>();
    private List<Note> notes = new ArrayList<>();
    private List<Test> tests = new ArrayList<>();
    private List<StudyHabit> habits = new ArrayList<>();
    private List<HabitLog> habitLogs = new ArrayList<>();

    // Fast lookup of courses by ID
    private final Map<Integer, Course> courseMap = new HashMap<>();

    public StudyMateService() {
        // CSV files live under a simple "data" folder in the working directory
        this.courseCsv = new CsvPersistenceManager<>("data/courses.csv", Course::parse);
        this.assignmentCsv = new CsvPersistenceManager<>("data/assignments.csv", Assignment::parse);

        this.jsonRepository = new JsonAppStateRepository("data/studymate.json");
        this.objectRepository = new ObjectStreamAppStateRepository("data/studymate.bin");

        try {
            loadAllData();
        } catch (Exception e) {
            System.err.println("Failed to load data: " + e.getMessage());
            // Proceed with empty data if loading fails
        }

        // If there was nothing on disk, populate some defaults
        DataInitializer.checkAndInitialize(this);
    }

    // ---------------- Basic persistence (Lab 5) ----------------

    public void loadAllData() throws IOException {
        courses = courseCsv.loadAll();
        assignments = assignmentCsv.loadAll();
        rebuildCourseMap();
    }

    public void saveAllData() throws IOException {
        courseCsv.saveAll(courses);
        assignmentCsv.saveAll(assignments);
    }

    private void rebuildCourseMap() {
        courseMap.clear();
        for (Course c : courses) {
            courseMap.put(c.getCourseId(), c);
        }
    }

    // ---------------- AppState conversion helpers (Lab 6) ----------------

    private AppState toAppState() {
        AppState state = new AppState();
        state.setCourses(new ArrayList<>(courses));
        state.setAssignments(new ArrayList<>(assignments));
        state.setNotes(new ArrayList<>(notes));
        state.setTests(new ArrayList<>(tests));
        state.setHabits(new ArrayList<>(habits));
        state.setHabitLogs(new ArrayList<>(habitLogs));
        return state;
    }

    private void restoreFromAppState(AppState state) {
        if (state == null) {
            return;
        }
        this.courses = new ArrayList<>(state.getCourses());
        this.assignments = new ArrayList<>(state.getAssignments());
        this.notes = new ArrayList<>(state.getNotes());
        this.tests = new ArrayList<>(state.getTests());
        this.habits = new ArrayList<>(state.getHabits());
        this.habitLogs = new ArrayList<>(state.getHabitLogs());
        rebuildCourseMap();
    }

    // ---------------- JSON & ObjectStream persistence (Lab 6) ----------------

    public void saveAsJson() throws IOException {
        jsonRepository.save(toAppState());
    }

    public void loadFromJson() throws IOException {
        AppState state = jsonRepository.load();
        restoreFromAppState(state);
    }

    public void saveAsBinary() throws IOException {
        objectRepository.save(toAppState());
    }

    public void loadFromBinary() throws IOException {
        AppState state = objectRepository.load();
        restoreFromAppState(state);
    }

    // ---------------- CRUD style operations ----------------

    public List<Course> getCourses() {
        return courses;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public Map<Integer, Course> getCourseMap() {
        return courseMap;
    }

    public Course getCourseById(int id) {
        return courseMap.get(id);
    }

    public void addCourse(Course course) throws DuplicateIdException {
        if (courseMap.containsKey(course.getCourseId())) {
            throw new DuplicateIdException("Course", course.getCourseId());
        }
        courses.add(course);
        courseMap.put(course.getCourseId(), course);
        autoSave();
    }

    public void addAssignment(Assignment assignment)
            throws DuplicateIdException, InvalidCourseException {
        if (!courseMap.containsKey(assignment.getCourseId())) {
            throw new InvalidCourseException(assignment.getCourseId());
        }

        boolean duplicate = assignments.stream()
                .anyMatch(a -> a.getAssignmentId() == assignment.getAssignmentId());
        if (duplicate) {
            throw new DuplicateIdException("Assignment", assignment.getAssignmentId());
        }
        assignments.add(assignment);
        autoSave();
    }

    private void autoSave() {
        try {
            saveAllData(); // CSV
            saveAsJson(); // JSON
            // saveAsBinary(); // Optional, maybe overkill for every edit
        } catch (IOException e) {
            System.err.println("Error auto-saving data: " + e.getMessage());
        }
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public void addTest(Test test) {
        tests.add(test);
    }

    public void addHabit(StudyHabit habit) {
        habits.add(habit);
    }

    public void addHabitLog(HabitLog log) {
        habitLogs.add(log);
    }

    // ---------------- Analytics using streams (Lab 4) ----------------

    /**
     * Returns assignments that have upcoming deadlines, sorted by due date then
     * priority.
     */
    public List<Assignment> getUpcomingDeadlines() {
        LocalDate now = LocalDate.now();
        return assignments.stream()
                .filter(a -> !a.isCompleted())
                .filter(a -> !a.getDueDate().isBefore(now))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Groups completed assignments by course and returns the count for each.
     */
    public Map<Course, Long> getCompletionCountsByCourse() {
        return assignments.stream()
                .filter(Assignment::isCompleted)
                .collect(Collectors.groupingBy(
                        a -> courseMap.get(a.getCourseId()),
                        Collectors.counting()));
    }

    // ---------------- Threads integration (Lab 8) ----------------

    /**
     * Starts a background analysis thread that computes the sum of credit hours
     * for all pending assignments.
     */
    public AnalysisThread startPendingCreditAnalysis() {
        AnalysisThread thread = new AnalysisThread(courseMap, assignments);
        thread.start();
        return thread;
    }
}
