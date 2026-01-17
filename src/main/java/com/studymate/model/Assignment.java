package com.studymate.model;

import com.studymate.interfaces.Persistable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a scheduled piece of work in a course.
 *
 * Implements Comparable so we can sort by due date and then by priority.
 */
public class Assignment implements Persistable, Comparable<Assignment>, Serializable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private int assignmentId;
    private int courseId; // Foreign Key (FK)
    private String title;
    private String description;
    private LocalDate dueDate; // Java Time API
    private int priority;      // 1 = highest priority
    private String status;     // e.g. Pending, Completed, In Progress

    public Assignment(int assignmentId,
                      int courseId,
                      String title,
                      String description,
                      LocalDate dueDate,
                      int priority,
                      String status) {
        this.assignmentId = assignmentId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    public Assignment() {
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return "Completed".equalsIgnoreCase(status);
    }

    // ---------- Persistable (CSV) ----------

    @Override
    public String toCsvRecord() {
        return assignmentId + "," +
                courseId + "," +
                title + "," +
                description + "," +
                DATE_FORMAT.format(dueDate) + "," +
                priority + "," +
                status;
    }

    public static Assignment parse(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid Assignment record format.");
        }

        int id = Integer.parseInt(parts[0]);
        int courseId = Integer.parseInt(parts[1]);
        LocalDate dueDate = LocalDate.parse(parts[4], DATE_FORMAT);
        int priority = Integer.parseInt(parts[5]);

        return new Assignment(id, courseId, parts[2], parts[3], dueDate, priority, parts[6]);
    }

    // ---------- Comparable & utility ----------

    @Override
    public int compareTo(Assignment other) {
        int cmp = this.dueDate.compareTo(other.dueDate);
        if (cmp != 0) return cmp;
        // Earlier (smaller) priority value should come first
        cmp = Integer.compare(this.priority, other.priority);
        if (cmp != 0) return cmp;
        return Integer.compare(this.assignmentId, other.assignmentId);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentId=" + assignmentId +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;
        Assignment that = (Assignment) o;
        return assignmentId == that.assignmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentId);
    }
}
