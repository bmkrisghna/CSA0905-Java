import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/** B1.1 - test table: normal, missing ID, damaged line, empty line, bad amount, missing file, half-written last line. */
public class PassStoreTest {
    static int pass = 0, fail = 0;
    static void check(String name, String why, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        if (ok) pass++; else fail++;
        System.out.printf("%-34s expected=%-38s actual=%-38s %s%n   verifies: %s%n", name, expected, actual, ok ? "PASS" : "FAIL", why);
    }

    public static void main(String[] args) throws Exception {
        Path f = Files.createTempFile("passes", ".csv");
        PassStore s = new PassStore(f);
        s.add("P1001", "192311001", "Arun", 12, 4500);
        s.add("P1002", "192311002", "Divya", 7, 3800);
        s.add("P1003", "192311003", "Kiran", 12, 4100);

        check("find existing pass", "normal case", "[P1002, 192311002, Divya, 7, 3800.0]", Arrays.toString(s.find("P1002")));
        check("find pass that does not exist", "search ends cleanly with null", null, s.find("P9999") == null ? null : "found");
        check("list route 12", "list returns every valid pass of the route", 2, s.listByRoute(12).size());

        Files.writeString(f, "P1004,DAMAGED LINE\n", StandardOpenOption.APPEND);                 // wrong number of fields
        Files.writeString(f, "\n", StandardOpenOption.APPEND);                                     // empty line
        Files.writeString(f, "P1005,192311005,Meena,5,abc\n", StandardOpenOption.APPEND);         // non-numeric amount
        s.add("P1006", "192311006", "Ravi", 12, 4200);
        check("find after damaged lines", "damaged lines do not stop the search", "[P1006, 192311006, Ravi, 12, 4200.0]", Arrays.toString(s.find("P1006")));
        check("find damaged pass P1004", "damaged line is ignored", null, s.find("P1004") == null ? null : "found");
        check("find pass with bad amount P1005", "non-numeric amount = damaged", null, s.find("P1005") == null ? null : "found");
        check("list route 12 after damage", "damaged lines are not listed", 3, s.listByRoute(12).size());

        Files.writeString(f, "P1007,192311007,Half", StandardOpenOption.APPEND);               // power failure: line cut in the middle, no newline
        check("incomplete last line", "an unfinished write is treated as damaged", null, s.find("P1007") == null ? null : "found");

        Path none = Path.of("this_file_does_not_exist.csv");
        check("missing file", "missing file handled, no crash", null, new PassStore(none).find("P1001") == null ? null : "found");
        Files.delete(f);
        System.out.println("\nPASSED " + pass + "  FAILED " + fail);
    }
}
