import java.io.IOException;
import java.util.*;

/** Q2 - three layers (controller -> service -> storage), user-defined exceptions, propagation, chaining. */
public class ExamPortal {

    // ---------- exception hierarchy (all checked: the caller MUST decide what to do) ----------
    static class ExamException extends Exception {
        ExamException(String m) { super(m); }
        ExamException(String m, Throwable cause) { super(m, cause); }
    }
    static class ExamClosedException extends ExamException {
        ExamClosedException(String m) { super(m); }
    }
    static class NotRegisteredException extends ExamException {
        NotRegisteredException(String m) { super(m); }
    }
    static class FileTooLargeException extends ExamException {
        FileTooLargeException(String m) { super(m); }
    }
    static class SubmissionStorageException extends ExamException {
        SubmissionStorageException(String m, Throwable cause) { super(m, cause); }
    }

    // ---------- storage layer ----------
    static class StorageLayer {
        void save(String fileName, long sizeKb, boolean keepCause) throws SubmissionStorageException {
            try {
                if (sizeKb > 500) throw new IOException("Disk quota exceeded");
                System.out.println("   [storage] saved " + fileName);
            } catch (IOException e) {
                if (keepCause) throw new SubmissionStorageException("Could not store " + fileName, e);   // chained
                throw new SubmissionStorageException("Could not store " + fileName, null);               // cause LOST
            }
        }
    }

    // ---------- service layer ----------
    static class ExamService {
        private final StorageLayer storage = new StorageLayer();
        private final Set<String> registered = Set.of("S101", "S102", "S103");

        void submit(String student, String fileName, long sizeKb, boolean windowOpen, boolean keepCause)
                throws ExamException {
            if (!windowOpen) throw new ExamClosedException("Exam window is closed");
            if (!registered.contains(student)) throw new NotRegisteredException(student + " is not registered for this course");
            if (sizeKb > 2000) throw new FileTooLargeException("File is " + sizeKb + " KB, limit is 2000 KB");
            storage.save(fileName, sizeKb, keepCause);          // may throw SubmissionStorageException
        }
    }

    // ---------- controller layer (finally handles the exception, shows a message) ----------
    static class ExamController {
        private final ExamService service = new ExamService();

        String handle(String student, String file, long kb, boolean open, boolean keepCause) {
            try {
                service.submit(student, file, kb, open, keepCause);
                return "SUCCESS: submission accepted";
            } catch (ExamClosedException | NotRegisteredException | FileTooLargeException e) {
                return "REJECTED: " + e.getMessage();
            } catch (SubmissionStorageException e) {
                System.out.println("   --- full chained stack trace ---");
                e.printStackTrace(System.out);
                System.out.println("   --- end ---");
                Throwable root = e;
                while (root.getCause() != null) root = root.getCause();
                return "FAILED: " + e.getMessage() + " | root cause: " + (root == e ? "LOST (no cause kept)" : root.getMessage());
            } catch (ExamException e) {
                return "ERROR: " + e.getMessage();
            }
        }
    }

    public static void main(String[] args) {
        ExamController c = new ExamController();
        Object[][] scenarios = {
            // student, file, sizeKb, windowOpen, keepCause, description
            {"S101", "a1.pdf", 120L,  true,  true,  "valid submission"},
            {"S101", "a2.pdf", 120L,  false, true,  "window closed"},
            {"S999", "a3.pdf", 120L,  true,  true,  "not registered"},
            {"S102", "a4.pdf", 3000L, true,  true,  "file too large"},
            {"S103", "a5.pdf", 800L,  true,  true,  "storage fails, cause KEPT"},
            {"S103", "a6.pdf", 800L,  true,  false, "storage fails, cause LOST"},
        };
        int n = 1;
        for (Object[] s : scenarios) {
            System.out.println("Scenario " + n++ + " (" + s[5] + ")");
            String result = c.handle((String) s[0], (String) s[1], (Long) s[2], (Boolean) s[3], (Boolean) s[4]);
            System.out.println("   Student sees: " + result);
        }
    }
}
