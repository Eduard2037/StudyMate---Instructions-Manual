package com.studymate.model;

import com.studymate.interfaces.Persistable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Core domain entity representing a course.
 */
public class Course implements Persistable, Serializable {

    private int courseId;
    private String courseName;
    private String instructorName;
    private String semester;
    private int creditHours;
    private String description;

    public Course(int courseId,
            String courseName,
            String instructorName,
            String semester,
            int creditHours,
            String description) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorName = instructorName;
        this.semester = semester;
        this.creditHours = creditHours;
        this.description = description;
    }

    public Course() {
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) {
        this.creditHours = creditHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ---------- Persistable (CSV) ----------
    @Override
    public String toCsvRecord() {
        // Simple CSV – assumes no commas in text fields.
        return courseId + "," +
                courseName + "," +
                instructorName + "," +
                semester + "," +
                creditHours + "," +
                description;
    }

    // Helper method for CsvPersistenceManager to reconstruct the object
    public static Course parse(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid Course record format.");
        }
        int id = Integer.parseInt(parts[0]);
        int credits = Integer.parseInt(parts[4]);

        // Reconstruct description if it contained commas
        String description = parts[5];
        if (parts.length > 6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 5; i < parts.length; i++) {
                if (i > 5)
                    sb.append(",");
                sb.append(parts[i]);
            }
            description = sb.toString();
        }

        return new Course(id, parts[1], parts[2], parts[3], credits, description);
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", instructorName='" + instructorName + '\'' +
                ", semester='" + semester + '\'' +
                ", creditHours=" + creditHours +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Course))
            return false;
        Course course = (Course) o;
        return courseId == course.courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
