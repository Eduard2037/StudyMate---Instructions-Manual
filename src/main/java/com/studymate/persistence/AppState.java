package com.studymate.persistence;

import com.studymate.model.Assignment;
import com.studymate.model.Course;
import com.studymate.model.HabitLog;
import com.studymate.model.Note;
import com.studymate.model.StudyHabit;
import com.studymate.model.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializable snapshot of the application state.
 * This is what we write/read using JSON and Java Object Streams (Lab 6).
 */
public class AppState implements Serializable {

    private List<Course> courses = new ArrayList<>();
    private List<Assignment> assignments = new ArrayList<>();
    private List<Note> notes = new ArrayList<>();
    private List<Test> tests = new ArrayList<>();
    private List<StudyHabit> habits = new ArrayList<>();
    private List<HabitLog> habitLogs = new ArrayList<>();

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<Test> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests;
    }

    public List<StudyHabit> getHabits() {
        return habits;
    }

    public void setHabits(List<StudyHabit> habits) {
        this.habits = habits;
    }

    public List<HabitLog> getHabitLogs() {
        return habitLogs;
    }

    public void setHabitLogs(List<HabitLog> habitLogs) {
        this.habitLogs = habitLogs;
    }
}
