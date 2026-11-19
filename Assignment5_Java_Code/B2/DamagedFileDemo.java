import java.nio.file.*;
import java.util.Arrays;

/** B2 - adversarial bus-pass file: wrong number of fields, non-numeric amount, empty line, unfinished line. */
public class DamagedFileDemo {
    public static void main(String[] args) throws Exception {
        Path f = Files.createTempFile("damaged", ".csv");
        Files.writeString(f, String.join("\n",
                "P2001,192311001,Arun,12,4500.0",            // good
                "P2002,192311002,Divya,7",                   // 4 fields (field missing)
                "P2003,192311003,Kiran,12,4100.0,EXTRA",     // 6 fields (extra comma)
                "P2004,192311004,Meena,5,FIVE-HUNDRED",      // non-numeric amount
                "",                                          // empty line
                "P2005,192311005,Ravi,12,4200.0",            // good, after the damage
                "P2006,192311006,Hal") );                    // line cut in the middle (power failure), no newline
        System.out.println("File content:\n" + Files.readString(f) + "\n");

        PassStore s = new PassStore(f);
        for (String id : new String[]{"P2001", "P2002", "P2003", "P2004", "P2005", "P2006", "P2999"})
            System.out.printf("find(%s) -> %s%n", id, Arrays.toString(s.find(id)));
        System.out.println("Valid passes on route 12: " + s.listByRoute(12).size() + " (expected 2: P2001 and P2005)");
        Files.delete(f);
    }
}
