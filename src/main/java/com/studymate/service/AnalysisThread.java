package com.studymate.service;

import com.studymate.model.Assignment;
import com.studymate.model.Course;

import java.util.List;
import java.util.Map;

/**
 * Thread for parallelizing a somewhat expensive analysis:
 * sum of the credit hours associated with all pending assignments.
 */
public class AnalysisThread extends Thread {

    private final Map<Integer, Course> courseMap;
    private final List<Assignment> assignments;
    private long totalPendingCreditHours = 0; // result

    public AnalysisThread(Map<Integer, Course> courseMap, List<Assignment> assignments) {
        this.courseMap = courseMap;
        this.assignments = assignments;
        setName("StudyMate-AnalysisThread");
    }

    @Override
    public void run() {
        long total = 0;
        for (Assignment a : assignments) {
            if (!a.isCompleted()) {
                Course course = courseMap.get(a.getCourseId());
                int credits = (course != null) ? course.getCreditHours() : 0;
                // artificial inner loop to fake work
                for (int i = 0; i < 50_000; i++) {
                    total += credits;
                }
            }
        }
        this.totalPendingCreditHours = total;
        System.out.println("Analysis finished. Total pending credit-hours-units calculated.");
    }

    public long getTotalPendingCreditHours() {
        return totalPendingCreditHours;
    }
}
