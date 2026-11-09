import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarksImporter {
    private static final Logger LOGGER = Logger.getLogger(MarksImporter.class.getName());

    // Custom unchecked exception used when a student ID appears more than once.
    static class DuplicateStudentException extends RuntimeException {
        public DuplicateStudentException(String message) {
            super(message);
        }
    }

    /*
     * Recovery choice: skip a bad record and continue with the next line.
     * IOException is checked, so this method declares it with throws.
     */
    static void importMarks(String filename) throws IOException {
        Set<String> seen = new HashSet<>();

        // try-with-resources closes the reader automatically, even if an exception occurs.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    String[] parts = line.split(",", -1);

                    // TODO 1 completed: require exactly two fields.
                    if (parts.length != 2 || parts[0].trim().isEmpty()
                            || parts[1].trim().isEmpty()) {
                        throw new IllegalArgumentException(
                                "Line " + lineNumber + ": expected format STUDENT_ID, MARK");
                    }

                    String studentId = parts[0].trim();
                    int mark;

                    // TODO 2 completed: parse the mark and validate the range 0..100.
                    try {
                        mark = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException ex) {
                        LOGGER.log(Level.WARNING,
                                "Invalid numeric mark at line " + lineNumber + ": " + line, ex);
                        System.out.println("Line " + lineNumber
                                + ": invalid mark '" + parts[1].trim()
                                + "'. Enter a whole number from 0 to 100. Record skipped.");
                        continue;
                    }

                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException(
                                "Mark must be between 0 and 100, but was " + mark);
                    }

                    // TODO 3 completed: identify duplicate IDs.
                    if (seen.contains(studentId)) {
                        throw new DuplicateStudentException(
                                "Duplicate student ID '" + studentId + "' at line " + lineNumber);
                    }
                    seen.add(studentId);

                    // TODO 4 completed: display an accepted record.
                    System.out.println("Accepted: " + studentId + " -> " + mark);

                } catch (DuplicateStudentException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                    System.out.println("Line " + lineNumber + ": duplicate student ID '"
                            + extractId(line) + "'. Record skipped.");
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.WARNING,
                            "Invalid record at line " + lineNumber + ": " + line, ex);
                    System.out.println("Line " + lineNumber + ": " + ex.getMessage()
                            + ". Record skipped.");
                }
            }
        }
    }

    private static String extractId(String line) {
        String[] parts = line.split(",", -1);
        return parts.length > 0 ? parts[0].trim() : line;
    }

    public static void main(String[] args) {
        // Change this to "absent.txt" when performing test T5.
        String filename = "marks.txt";

        try {
            importMarks(filename);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Could not open marks file: " + filename, ex);
            System.out.println("Marks file not found; check the file name.");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "I/O error while importing marks.", ex);
            System.out.println("The marks file could not be read. Check the file and try again.");
        } finally {
            System.out.println("Import attempt finished");
        }
    }
}
