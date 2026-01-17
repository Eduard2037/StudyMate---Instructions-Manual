package com.studymate.model;

import com.studymate.interfaces.IAnalyzable;
import com.studymate.interfaces.Persistable;

import java.io.Serializable;
import java.util.Objects;

/**
 * High-level description of a recurring study habit, e.g. "Read 10 pages/day".
 */
public class StudyHabit implements Persistable, IAnalyzable, Serializable {

    private int habitId;
    private String name;
    private String description;
    private int weeklyTarget; // target number of repetitions/units per week

    public StudyHabit(int habitId, String name, String description, int weeklyTarget) {
        this.habitId = habitId;
        this.name = name;
        this.description = description;
        this.weeklyTarget = weeklyTarget;
    }

    public StudyHabit() {
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeeklyTarget() {
        return weeklyTarget;
    }

    public void setWeeklyTarget(int weeklyTarget) {
        this.weeklyTarget = weeklyTarget;
    }

    @Override
    public String toCsvRecord() {
        return habitId + "," +
                name + "," +
                description + "," +
                weeklyTarget;
    }

    public static StudyHabit parse(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid StudyHabit record format.");
        }
        int id = Integer.parseInt(parts[0]);
        int weeklyTarget = Integer.parseInt(parts[3]);
        return new StudyHabit(id, parts[1], parts[2], weeklyTarget);
    }

    @Override
    public double computeScore() {
        // Habit alone has no intrinsic score – just return target.
        return weeklyTarget;
    }

    @Override
    public String toString() {
        return "StudyHabit{" +
                "habitId=" + habitId +
                ", name='" + name + '\'' +
                ", weeklyTarget=" + weeklyTarget +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudyHabit)) return false;
        StudyHabit that = (StudyHabit) o;
        return habitId == that.habitId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(habitId);
    }
}
