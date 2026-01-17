package com.studymate.model;

import com.studymate.interfaces.Persistable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A concrete log entry for a given habit on a particular day.
 */
public class HabitLog implements Persistable, Serializable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private int logId;
    private int habitId;
    private LocalDate date;
    private int amount; // number of repetitions / minutes / etc.
    private String note;

    public HabitLog(int logId, int habitId, LocalDate date, int amount, String note) {
        this.logId = logId;
        this.habitId = habitId;
        this.date = date;
        this.amount = amount;
        this.note = note;
    }

    public HabitLog() {
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toCsvRecord() {
        return logId + "," +
                habitId + "," +
                DATE_FORMAT.format(date) + "," +
                amount + "," +
                note;
    }

    public static HabitLog parse(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid HabitLog record format.");
        }
        int id = Integer.parseInt(parts[0]);
        int habitId = Integer.parseInt(parts[1]);
        LocalDate date = LocalDate.parse(parts[2], DATE_FORMAT);
        int amount = Integer.parseInt(parts[3]);
        return new HabitLog(id, habitId, date, amount, parts[4]);
    }

    @Override
    public String toString() {
        return "HabitLog{" +
                "logId=" + logId +
                ", habitId=" + habitId +
                ", date=" + date +
                ", amount=" + amount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HabitLog)) return false;
        HabitLog that = (HabitLog) o;
        return logId == that.logId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId);
    }
}
