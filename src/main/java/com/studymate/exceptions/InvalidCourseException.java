package com.studymate.exceptions;

// Thrown when an Assignment, Test, or Note references a non-existent Course ID (FK violation).
public class InvalidCourseException extends Exception {
    public InvalidCourseException(int courseId) {
        super("Operation failed: Course ID " + courseId + " does not exist.");
    }
}