package com.studymate.persistence;

import com.studymate.model.Assignment;
import com.studymate.model.Course;
import com.studymate.service.StudyMateService;

import java.time.LocalDate;

/**
 * Utility class that populates the application with a small set of sample data
 * if no course data was found on disk.
 */
public class DataInitializer {

    public static void checkAndInitialize(StudyMateService service) {
        // If the service already has courses, do nothing.
        if (!service.getCourseMap().isEmpty()) {
            return;
        }

        System.out.println("No courses found on disk – initializing sample data...");

        try {
            Course calculus = new Course(
                    101,
                    "Calculus I",
                    "Dr. Smith",
                    "Fall 2025",
                    3,
                    "Introduction to limits and derivatives.");
            Course prog3 = new Course(
                    102,
                    "Programming III",
                    "Dr. Spataru",
                    "Fall 2025",
                    5,
                    "Java, collections, streams, and threads.");

            service.addCourse(calculus);
            service.addCourse(prog3);

            Assignment a1 = new Assignment(
                    1,
                    101,
                    "Limits Worksheet",
                    "Solve problems 1–20 from chapter 1.",
                    LocalDate.now().plusDays(5),
                    1,
                    "Pending");
            Assignment a2 = new Assignment(
                    2,
                    101,
                    "Derivatives Quiz",
                    "Prepare for in-class quiz.",
                    LocalDate.now().plusDays(10),
                    2,
                    "Pending");
            Assignment a3 = new Assignment(
                    3,
                    102,
                    "Streams Lab",
                    "Implement stream-based analytics.",
                    LocalDate.now().plusDays(3),
                    1,
                    "In Progress");

            service.addAssignment(a1);
            service.addAssignment(a2);
            service.addAssignment(a3);

            service.saveAllData();
            System.out.println("Sample data initialized and saved.");

        } catch (Exception e) {
            System.err.println("Failed to initialize sample data: " + e.getMessage());
        }
    }
}
