package com.studymate;

import com.studymate.core.Application;
import com.studymate.exceptions.DuplicateIdException;
import com.studymate.exceptions.InvalidCourseException;
import com.studymate.io.InputDevice;
import com.studymate.io.OutputDevice;
import com.studymate.model.Assignment;
import com.studymate.model.Course;
import com.studymate.service.StudyMateService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;

/**
 * Entry point for the console-based StudyMate application.
 *
 * Existing Lab 1–2 modes:
 *   words [sentence...]
 *   numbers N
 *   play R
 *
 * Added modes (Lab 5 exceptions demo):
 *   file-read   -> prompts for file name and prints content (re-prompts on exception)
 *   file-write  -> prompts for file name and writes 10 random numbers (re-prompts on exception)
 *
 * Default (no arguments):
 *   - shows a robust exception demo using the custom exceptions
 *   - saves CSV, JSON and binary snapshots
 *   - runs the Lab 1 greeting demo
 */
public class Main {

    public static void main(String[] args) {
        StudyMateService service = new StudyMateService();
        InputDevice in = new InputDevice(System.in);
        OutputDevice out = new OutputDevice(System.out);
        Application app = new Application(in, out, service);

        // Print program arguments using OutputDevice (Lab 2.3)
        out.writeMessage("--- Program arguments (" + args.length + ") ---");
        for (int i = 0; i < args.length; i++) {
            out.writeMessage("arg[" + i + "] = " + args[i]);
        }

        // ---------- Argument-based dispatch ----------
        if (args.length > 0) {
            String mode = args[0].toLowerCase();
            switch (mode) {
                case "words": {
                    String sentence;
                    if (args.length >= 2) {
                        StringBuilder sb = new StringBuilder();
                        for (int iArg = 1; iArg < args.length; iArg++) {
                            if (iArg > 1) sb.append(' ');
                            sb.append(args[iArg]);
                        }
                        sentence = sb.toString();
                    } else {
                        out.writeMessage("Enter a sentence:");
                        sentence = in.getLine();
                    }
                    app.exampleHistogram(sentence);
                    return;
                }

                case "numbers": {
                    int n = 10;
                    if (args.length >= 2) {
                        try {
                            n = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex) {
                            out.writeMessage("Invalid N, defaulting to 10.");
                        }
                    }
                    app.randomArraySort(n);
                    return;
                }

                case "play": {
                    int rounds = 5;
                    if (args.length >= 2) {
                        try {
                            rounds = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex) {
                            out.writeMessage("Invalid rounds value, defaulting to 5.");
                        }
                    }
                    app.playGame(rounds);
                    return;
                }

                case "file-read": {
                    app.askUserForFile();
                    return;
                }

                case "file-write": {
                    app.writeRandomNumbers();
                    return;
                }

                default:
                    out.writeMessage("Unknown mode: " + mode + ". Running default StudyMate demo...");
            }
        }

        // ---------- Default behaviour: StudyMate domain + exception demo ----------
        runDefaultDemo(service, app, out);
    }

    private static void runDefaultDemo(StudyMateService service, Application app, OutputDevice out) {
        out.writeMessage("\n--- StudyMate loaded ---");
        out.writeMessage("Courses: " + service.getCourses().size());
        out.writeMessage("Assignments: " + service.getAssignments().size());

        // 1) Demonstrate DuplicateIdException (custom) and recovery
        out.writeMessage("\n--- Custom Exception Demo #1: DuplicateIdException ---");
        try {
            // Intentionally try to add a course with an ID that most likely already exists (from initializer)
            service.addCourse(new Course(
                    101,
                    "Duplicate Test Course",
                    "Test Instructor",
                    "Fall 2025",
                    3,
                    "This should trigger DuplicateIdException."
            ));
            out.writeMessage("Unexpected: Course 101 added without error (data may have been empty).");
        } catch (DuplicateIdException e) {
            out.writeMessage("Caught expected exception: " + e.getMessage());

            // Recovery plan: choose a free ID and add a valid course
            int newId = findFreeCourseId(service, 200);
            try {
                service.addCourse(new Course(
                        newId,
                        "Recovered Course " + newId,
                        "Recovery Instructor",
                        "Fall 2025",
                        3,
                        "Added after catching DuplicateIdException."
                ));
                out.writeMessage("Recovery succeeded: created course with ID " + newId);
            } catch (DuplicateIdException ex) {
                // Should be extremely unlikely because we search for a free id,
                // but we still show robust handling.
                out.writeMessage("Recovery failed unexpectedly: " + ex.getMessage());
            }
        }

        // 2) Demonstrate InvalidCourseException (custom) and recovery
        out.writeMessage("\n--- Custom Exception Demo #2: InvalidCourseException ---");
        int nextAssignmentId = nextAssignmentId(service);

        Assignment bad = new Assignment(
                nextAssignmentId,
                999999, // intentionally invalid course id
                "Invalid Course Demo",
                "This assignment references a missing course and should fail.",
                LocalDate.now().plusDays(7),
                1,
                "Pending"
        );

        try {
            service.addAssignment(bad);
            out.writeMessage("Unexpected: assignment added with invalid course id (data may have been empty).");
        } catch (InvalidCourseException e) {
            out.writeMessage("Caught expected exception: " + e.getMessage());

            // Recovery: attach to an existing course id
            Integer validCourseId = service.getCourses().stream()
                    .map(Course::getCourseId)
                    .findFirst()
                    .orElse(null);

            if (validCourseId == null) {
                out.writeMessage("No courses available to recover. (This should not happen.)");
            } else {
                bad.setCourseId(validCourseId);
                try {
                    service.addAssignment(bad);
                    out.writeMessage("Recovery succeeded: assignment added under course " + validCourseId);
                } catch (DuplicateIdException | InvalidCourseException ex) {
                    out.writeMessage("Recovery failed unexpectedly: " + ex.getMessage());
                }
            }
        } catch (DuplicateIdException e) {
            out.writeMessage("Unexpected DuplicateIdException: " + e.getMessage());
        }

        // 3) Demonstrate DuplicateIdException for assignments too (and recovery)
        out.writeMessage("\n--- Custom Exception Demo #3: DuplicateIdException (Assignment) ---");
        if (!service.getAssignments().isEmpty()) {
            int existingId = service.getAssignments().get(0).getAssignmentId();
            int courseId = service.getAssignments().get(0).getCourseId();

            try {
                service.addAssignment(new Assignment(
                        existingId, // duplicate id
                        courseId,
                        "Duplicate Assignment ID",
                        "This should trigger DuplicateIdException.",
                        LocalDate.now().plusDays(1),
                        2,
                        "Pending"
                ));
                out.writeMessage("Unexpected: duplicate assignment id was accepted.");
            } catch (DuplicateIdException e) {
                out.writeMessage("Caught expected exception: " + e.getMessage());
                int newId = nextAssignmentId(service);
                try {
                    service.addAssignment(new Assignment(
                            newId,
                            courseId,
                            "Recovered Assignment " + newId,
                            "Added after catching duplicate assignment ID.",
                            LocalDate.now().plusDays(1),
                            2,
                            "Pending"
                    ));
                    out.writeMessage("Recovery succeeded: assignment added with ID " + newId);
                } catch (DuplicateIdException | InvalidCourseException ex) {
                    out.writeMessage("Recovery failed unexpectedly: " + ex.getMessage());
                }
            } catch (InvalidCourseException e) {
                out.writeMessage("Unexpected InvalidCourseException: " + e.getMessage());
            }
        } else {
            out.writeMessage("No assignments exist yet – skipping duplicate assignment demo.");
        }

        // 4) Demonstrate saving with try/catch for IOExceptions (language exception handling)
        out.writeMessage("\n--- Persistence Demo (CSV + JSON + ObjectStream) ---");
        try {
            service.saveAllData();
            out.writeMessage("Saved CSV to data/*.csv");

            service.saveAsJson();
            out.writeMessage("Saved JSON snapshot to data/studymate.json");

            service.saveAsBinary();
            out.writeMessage("Saved binary snapshot to data/studymate.bin");

        } catch (IOException e) {
            out.writeMessage("IOException while saving: " + e.getMessage());
            e.printStackTrace();
        }

        // 5) Show some analytics output
        out.writeMessage("\n--- Analytics Demo (streams) ---");
        out.writeMessage("Upcoming deadlines count: " + service.getUpcomingDeadlines().size());

        // 6) Run the Lab 1 greeting demo for completeness
        out.writeMessage("\n--- Lab 1 greeting demo ---");
        app.run();

        out.writeMessage("\nDone.");
    }

    private static int findFreeCourseId(StudyMateService service, int startId) {
        int id = startId;
        while (service.getCourseById(id) != null) {
            id++;
        }
        return id;
    }

    private static int nextAssignmentId(StudyMateService service) {
        return service.getAssignments().stream()
                .map(Assignment::getAssignmentId)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
    }
}
