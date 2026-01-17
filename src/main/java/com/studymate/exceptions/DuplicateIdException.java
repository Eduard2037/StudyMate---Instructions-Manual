package com.studymate.exceptions;

// Thrown when trying to create an entity with an ID that already exists.
public class DuplicateIdException extends Exception {
    public DuplicateIdException(String entityType, int id) {
        super(String.format("Failed to create %s: ID %d already exists.", entityType, id));
    }
}