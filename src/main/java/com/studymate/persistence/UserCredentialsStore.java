package com.studymate.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple, file-based credential storage for Lab 9.
 *
 * Format of each line:
 *   email|name|password
 *
 * This is intentionally minimal and NOT secure – for teaching purposes only.
 */
public class UserCredentialsStore {

    private final Path file;

    public UserCredentialsStore(String filePath) {
        this.file = Paths.get(filePath);
    }

    public synchronized void registerUser(String email, String name, String password)
            throws IOException, IllegalArgumentException {
        Map<String, String> users = loadAllUsers();
        if (users.containsKey(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }

        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                file,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(email + "|" + name + "|" + password);
            writer.newLine();
        }
    }

    public synchronized boolean authenticate(String email, String password) throws IOException {
        Map<String, String> users = loadAllUsers();
        String storedPassword = users.get(email);
        return storedPassword != null && storedPassword.equals(password);
    }

    private Map<String, String> loadAllUsers() throws IOException {
        Map<String, String> users = new HashMap<>();
        if (!Files.exists(file)) {
            return users;
        }

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|"); // escaped for regex
                if (parts.length >= 3) {
                    users.put(parts[0], parts[2]);
                }
            }
        }
        return users;
    }
}
