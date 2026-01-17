package com.studymate.persistence;

import java.io.IOException;

/**
 * Abstraction layer for persistence (Lab 6).
 * Multiple implementations (CSV, JSON, ObjectStream) can exist.
 */
public interface AppStateRepository {

    void save(AppState state) throws IOException;

    AppState load() throws IOException;
}
