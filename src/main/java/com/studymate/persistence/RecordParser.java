// persistence/RecordParser.java (New file, or nested interface)
package com.studymate.persistence;

// Functional interface to handle the parsing logic specific to each entity.
@FunctionalInterface
public interface RecordParser<T> {
    T parse(String csvLine);
}