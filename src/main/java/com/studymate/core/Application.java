package com.studymate.core;

import com.studymate.io.InputDevice;
import com.studymate.io.OutputDevice;
import com.studymate.service.StudyMateService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Backwards compatible Lab 1–5 style Application abstraction.
 * This class is intentionally simple and console-based; it co-exists with the
 * richer StudyMateService and JavaFX UI.
 */
public class Application {

    private final InputDevice inputDevice;
    private final OutputDevice outputDevice;
    private final StudyMateService service;

    public Application(InputDevice inputDevice, OutputDevice outputDevice, StudyMateService service) {
        this.inputDevice = inputDevice;
        this.outputDevice = outputDevice;
        this.service = service;
    }

    /**
     * Minimal demo run – prints a greeting and number of loaded courses.
     */
    public void run() {
        outputDevice.writeMessage("Application started.");
        outputDevice.writeMessage("Today’s lucky numbers are: " +
                inputDevice.nextInt() + ", " + inputDevice.nextInt());

        outputDevice.writeMessage("Loaded courses in StudyMate: " +
                service.getCourses().size());
    }

    // ---------------- Lab 1 – Silly Game ----------------

    /**
     * Plays the silly game described in Lab 1:
     * two players pick random numbers in [1,100] until one reaches roundsToWin wins.
     */
    public void playGame(int roundsToWin) {
        int scoreA = 0;
        int scoreB = 0;

        outputDevice.writeMessage("Starting silly game. First to " + roundsToWin + " wins.");

        while (scoreA < roundsToWin && scoreB < roundsToWin) {
            int a = inputDevice.nextInt();
            int b = inputDevice.nextInt();

            int roundWinner = determineRoundWinner(a, b);

            if (roundWinner == 0) {
                scoreA += 2;
                scoreB += 2;
                outputDevice.writeMessage("Both chose " + a + ". Each gets 2 points.");
            } else if (roundWinner < 0) {
                scoreA++;
                outputDevice.writeMessage("A wins the round: A=" + a + ", B=" + b);
            } else {
                scoreB++;
                outputDevice.writeMessage("B wins the round: A=" + a + ", B=" + b);
            }
            outputDevice.writeMessage("Score: A=" + scoreA + " B=" + scoreB);
        }

        if (scoreA > scoreB) {
            outputDevice.writeMessage("Player A wins the game!");
        } else if (scoreB > scoreA) {
            outputDevice.writeMessage("Player B wins the game!");
        } else {
            outputDevice.writeMessage("The game is a tie!");
        }
    }

    /**
     * @return -1 if A wins, +1 if B wins, 0 if both get two points.
     */
    private int determineRoundWinner(int a, int b) {
        if (a == b) {
            return 0;
        }
        int H = Math.max(a, b);
        int S = Math.min(a, b);

        if (S >= 10 && H % S == 0) {
            // smaller number wins
            return (S == a) ? -1 : +1;
        } else {
            // higher number wins
            return (H == a) ? -1 : +1;
        }
    }

    // ---------------- Lab 2 – Arrays & Strings ----------------

    /**
     * Generates N random numbers and prints them before and after in-place sorting.
     */
    public void randomArraySort(int n) {
        int[] numbers = inputDevice.getNumbers(n);
        outputDevice.writeMessage("Random numbers: " + Arrays.toString(numbers));
        Arrays.sort(numbers);
        outputDevice.writeMessage("Sorted numbers: " + Arrays.toString(numbers));
    }

    /**
     * Demonstrates the word-size histogram logic required in Lab 2.
     */
    public void exampleHistogram(String sentence) {
        int[] hist = wordSizeHistogram(sentence);
        StringBuilder sb = new StringBuilder("Word length histogram: ");
        for (int i = 1; i < hist.length; i++) {
            if (hist[i] > 0) {
                sb.append(i).append("->").append(hist[i]).append("  ");
            }
        }
        outputDevice.writeMessage(sb.toString());
    }

    /**
     * Computes how many words of each size exist in the sentence.
     * Index i contains the number of words of size i.
     */
    public int[] wordSizeHistogram(String sentence) {
        String trimmed = sentence.trim();
        if (trimmed.isEmpty()) {
            return new int[1];
        }
        String[] words = trimmed.split("\\s+");
        int max = 0;
        for (String w : words) {
            max = Math.max(max, w.length());
        }
        int[] hist = new int[max + 1];
        for (String w : words) {
            hist[w.length()]++;
        }
        return hist;
    }

    // ---------------- Lab 5 – Exceptions + Files demo ----------------

    /**
     * Lab 5.3 #1:
     * Reads a file name from the InputDevice and tries to open it for reading.
     * If it fails, it prompts the user again.
     */
    public void askUserForFile() {
        outputDevice.writeMessage("Enter a file name to read (try a wrong name first to see exceptions):");
        while (true) {
            String fileName = inputDevice.getLine().trim();
            if (fileName.isEmpty()) {
                outputDevice.writeMessage("Please enter a non-empty file name:");
                continue;
            }

            try (InputStream in = new FileInputStream(fileName);
                 Scanner scanner = new Scanner(in)) {

                outputDevice.writeMessage("---- FILE CONTENT BEGIN ----");
                while (scanner.hasNextLine()) {
                    outputDevice.writeMessage(scanner.nextLine());
                }
                outputDevice.writeMessage("---- FILE CONTENT END ----");
                break;

            } catch (FileNotFoundException e) {
                outputDevice.writeMessage("File not found: " + fileName + ". Try again:");
                e.printStackTrace();
            } catch (Exception e) {
                outputDevice.writeMessage("Could not read file: " + e.getMessage() + ". Try again:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Lab 5.3 #2:
     * Reads a file name from the InputDevice and tries to open it for writing.
     * If it fails, it prompts the user again. Writes 10 random numbers if successful.
     */
    public void writeRandomNumbers() {
        outputDevice.writeMessage("Enter a file name to write (try an invalid path to see exceptions):");
        while (true) {
            String fileName = inputDevice.getLine().trim();
            if (fileName.isEmpty()) {
                outputDevice.writeMessage("Please enter a non-empty file name:");
                continue;
            }

            try (OutputStream out = new FileOutputStream(fileName);
                 PrintWriter writer = new PrintWriter(out)) {

                for (int i = 0; i < 10; i++) {
                    writer.println(inputDevice.nextInt());
                }
                writer.flush();

                outputDevice.writeMessage("Wrote 10 random numbers to: " + fileName);
                break;

            } catch (FileNotFoundException e) {
                outputDevice.writeMessage("Could not open file for writing: " + fileName + ". Try again:");
                e.printStackTrace();
            } catch (Exception e) {
                outputDevice.writeMessage("Could not write file: " + e.getMessage() + ". Try again:");
                e.printStackTrace();
            }
        }
    }
}
