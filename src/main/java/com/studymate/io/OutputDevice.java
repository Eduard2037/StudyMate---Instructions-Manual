package com.studymate.io;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Lab 1–5 style output abstraction.
 * Wraps an OutputStream and exposes a simple writeMessage API.
 */
public class OutputDevice {

    private final OutputStream out;
    private final PrintWriter writer;

    public OutputDevice(OutputStream out) {
        this.out = out;
        this.writer = new PrintWriter(out, true);
    }

    public void writeMessage(String message) {
        writer.println(message);
    }
}
