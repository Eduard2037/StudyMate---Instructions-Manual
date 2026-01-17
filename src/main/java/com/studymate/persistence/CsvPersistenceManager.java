package com.studymate.persistence;

import com.studymate.interfaces.Persistable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic CSV persistence manager used for Lab 5 style text-file
 * storage of entities that implement {@link Persistable}.
 */
public class CsvPersistenceManager<T extends Persistable> {

    private final Path filePath;
    private final RecordParser<T> parser;

    public CsvPersistenceManager(String filePath, RecordParser<T> parser) {
        this.filePath = Paths.get(filePath);
        this.parser = parser;
    }

    /**
     * Reads all entities from the underlying CSV file.
     */
    public List<T> loadAll() throws IOException {
        List<T> result = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return result;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                result.add(parser.parse(line));
            }
        }
        return result;
    }

    /**
     * Writes all entities to the CSV file, overwriting any existing content.
     */
    public void saveAll(List<T> entities) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            for (T entity : entities) {
                writer.println(entity.toCsvRecord());
            }
        }
    }
}
