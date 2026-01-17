package com.studymate.io;

import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

/**
 * Lab 1–5 style input abstraction.
 * Wraps an InputStream and also provides helper methods used in the early labs.
 */
public class InputDevice {

    private final InputStream in;
    private final Scanner scanner;
    private final Random random;

    public InputDevice(InputStream in) {
        this.in = in;
        this.scanner = new Scanner(in);
        this.random = new Random();
    }

    /**
     * For Lab 1 – identifies type of this input device.
     */
    public String getType() {
        return "console+random";
    }

    /**
     * Lab 1: returns a random integer between 1 and 100 (inclusive).
     */
    public int nextInt() {
        return 1 + random.nextInt(100);
    }

    /**
     * Lab 2: returns an array of N random integers between 1 and 100.
     */
    public int[] getNumbers(int n) {
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = nextInt();
        }
        return values;
    }

    /**
     * Lab 2: returns a whole line of text.
     */
    public String getLine() {
        return scanner.nextLine();
    }

    /**
     * Utility method to read a single int from the underlying stream
     * with basic validation / reprompting.
     */
    public int readInt() {
        while (true) {
            String token = scanner.nextLine().trim();
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException ex) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }
}
