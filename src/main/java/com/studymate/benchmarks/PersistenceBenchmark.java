package com.studymate.benchmarks;

import com.studymate.model.Assignment;
import com.studymate.model.Course;
import com.studymate.persistence.AppState;
import com.studymate.persistence.JsonAppStateRepository;
import com.studymate.persistence.ObjectStreamAppStateRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Small benchmark used to compare JSON vs ObjectStream file size and
 * (approximate) load times (Lab 6).
 */
public class PersistenceBenchmark {

    public static void main(String[] args) throws IOException {
        // Build a sample AppState with some data
        AppState state = new AppState();
        List<Course> courses = new ArrayList<>();
        List<Assignment> assignments = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Course c = new Course(
                    1000 + i,
                    "Course " + i,
                    "Instructor " + i,
                    "S" + (2025 + i % 2),
                    3 + (i % 3),
                    "Sample course " + i);
            courses.add(c);

            for (int j = 0; j < 10; j++) {
                assignments.add(new Assignment(
                        i * 10 + j,
                        c.getCourseId(),
                        "Assignment " + j + " for " + c.getCourseName(),
                        "Auto generated",
                        LocalDate.now().plusDays(j),
                        j % 3 + 1,
                        "Pending"));
            }
        }
        state.setCourses(courses);
        state.setAssignments(assignments);

        JsonAppStateRepository jsonRepo = new JsonAppStateRepository("data/benchmark_state.json");
        ObjectStreamAppStateRepository objRepo = new ObjectStreamAppStateRepository("data/benchmark_state.bin");

        // JSON
        long t0 = System.currentTimeMillis();
        jsonRepo.save(state);
        long t1 = System.currentTimeMillis();
        jsonRepo.load();
        long t2 = System.currentTimeMillis();

        long jsonSaveMs = t1 - t0;
        long jsonLoadMs = t2 - t1;
        long jsonSize = Files.size(Paths.get("data/benchmark_state.json"));

        // Object stream
        long t3 = System.currentTimeMillis();
        objRepo.save(state);
        long t4 = System.currentTimeMillis();
        objRepo.load();
        long t5 = System.currentTimeMillis();

        long objSaveMs = t4 - t3;
        long objLoadMs = t5 - t4;
        long objSize = Files.size(Paths.get("data/benchmark_state.bin"));

        System.out.println("JSON:  size=" + jsonSize + " bytes, save=" + jsonSaveMs + " ms, load=" + jsonLoadMs + " ms");
        System.out.println("OBJ :  size=" + objSize + " bytes, save=" + objSaveMs + " ms, load=" + objLoadMs + " ms");
    }
}
