package com.studymate.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Binary persistence using Java Object Streams (Lab 6).
 */
public class ObjectStreamAppStateRepository implements AppStateRepository {

    private final Path file;

    public ObjectStreamAppStateRepository(String filePath) {
        this.file = Paths.get(filePath);
    }

    @Override
    public void save(AppState state) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(state);
        }
    }

    @Override
    public AppState load() throws IOException {
        if (!Files.exists(file)) {
            return new AppState();
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            Object obj = in.readObject();
            return (AppState) obj;
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize AppState", e);
        }
    }
}
