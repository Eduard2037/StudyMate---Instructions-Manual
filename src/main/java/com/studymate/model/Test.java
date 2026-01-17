package com.studymate.model;

import com.studymate.interfaces.IAnalyzable;
import com.studymate.interfaces.Persistable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a test or exam for a given course.
 */
public class Test implements Persistable, IAnalyzable, Serializable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private int testId;
    private int courseId;
    private String name;
    private LocalDate date;
    private double maxScore;
    private double score; // student score (0 if not taken yet)

    public Test(int testId, int courseId, String name, LocalDate date, double maxScore, double score) {
        this.testId = testId;
        this.courseId = courseId;
        this.name = name;
        this.date = date;
        this.maxScore = maxScore;
        this.score = score;
    }

    public Test() {
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toCsvRecord() {
        return testId + "," +
                courseId + "," +
                name + "," +
                DATE_FORMAT.format(date) + "," +
                maxScore + "," +
                score;
    }

    public static Test parse(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid Test record format.");
        }
        int id = Integer.parseInt(parts[0]);
        int courseId = Integer.parseInt(parts[1]);
        LocalDate date = LocalDate.parse(parts[3], DATE_FORMAT);
        double maxScore = Double.parseDouble(parts[4]);
        double score = Double.parseDouble(parts[5]);
        return new Test(id, courseId, parts[2], date, maxScore, score);
    }

    // IAnalyzable: ratio between achieved score and max score (0..1)
    @Override
    public double computeScore() {
        if (maxScore <= 0) return 0.0;
        return score / maxScore;
    }

    @Override
    public String toString() {
        return "Test{" +
                "testId=" + testId +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", score=" + score +
                "/" + maxScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Test)) return false;
        Test test = (Test) o;
        return testId == test.testId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(testId);
    }
}
