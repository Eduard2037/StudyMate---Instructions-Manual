package com.studymate.model;

import com.studymate.interfaces.Persistable;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Simple personal note that can be attached to a course.
 */
public class Note implements Persistable, Serializable {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private int noteId;
    private int courseId;
    private String title;
    private String content;
    private LocalDate createdOn;

    public Note(int noteId, int courseId, String title, String content, LocalDate createdOn) {
        this.noteId = noteId;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.createdOn = createdOn;
    }

    public Note() {
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toCsvRecord() {
        return noteId + "," +
                courseId + "," +
                title + "," +
                content + "," +
                DATE_FORMAT.format(createdOn);
    }

    public static Note parse(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid Note record format.");
        }
        int id = Integer.parseInt(parts[0]);
        int courseId = Integer.parseInt(parts[1]);
        LocalDate created = LocalDate.parse(parts[4], DATE_FORMAT);
        return new Note(id, courseId, parts[2], parts[3], created);
    }

    @Override
    public String toString() {
        return "Note{" +
                "noteId=" + noteId +
                ", courseId=" + courseId +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note = (Note) o;
        return noteId == note.noteId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId);
    }
}
