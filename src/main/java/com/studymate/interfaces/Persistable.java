package com.studymate.interfaces;

// Lab 3: Defining a behavior interface
public interface Persistable {

    /**
     * Converts the current object state into a single CSV formatted string line.
     * The order of fields must be consistent with the fromCsvRecord implementation.
     * @return A String representing the object record (e.g., "1,Calculus I,Dr. Smith,Fall 2025,3,Advanced")
     */
    String toCsvRecord();

    /**
     * Creates a new instance of the implementing class from a CSV string line.
     * This method must be implemented as a static factory or handled via reflection/separate utility.
     * For simplicity in this structure, we'll assume a dedicated parser utility handles this,
     * or the concrete class provides a static method for it (which is messy). 
     * We will use a dedicated parser method in the Service layer for cleaner code.
     */
    // For now, this interface remains focused on the output side:
}