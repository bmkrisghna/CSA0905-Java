import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/**
 * B4.1 - migrate the old pass file to the database: validate -> batch insert in ONE transaction -> failure report.
 *   java -cp ".:lib/*" Migration sample_passes.csv             validate, then load into MySQL
 *   java Migration sample_passes.csv --dry-run                 validate only (no database needed)
 * Line format: passId,regNo,name,route,amount
 * Records that fail validation are written to migration_failures.txt and NOT loaded.
 */
public class Migration {

    /** returns null if the line is valid, otherwise the reason */
    static String validate(String line) {
        String[] f = line.split(",", -1);
        if (f.length != 5) return "expected 5 fields but found " + f.length;
        if (!f[0].matches("P\\d{3,8}")) return "bad pass id '" + f[0] + "'";
        if (!f[1].matches("\\d{9}")) return "register number must be 9 digits";
        if (f[2].isBlank() || f[2].length() > 60) return "name is empty or too long";
        try { if (Integer.parseInt(f[3].trim()) <= 0) return "route must be positive"; } catch (NumberFormatException e) { return "route is not a number"; }
        try { if (Double.parseDouble(f[4].trim()) <= 0) return "amount must be positive"; } catch (NumberFormatException e) { return "amount is not a number"; }
        return null;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) { System.out.println("usage: java Migration <passes.csv> [--dry-run]"); return; }
        boolean dry = Arrays.asList(args).contains("--dry-run");
        List<String[]> valid = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int lineNo = 0;
        try (BufferedReader r = Files.newBufferedReader(Path.of(args[0]))) {
            String line;
            while ((line = r.readLine()) != null) {
                lineNo++;
                String why = validate(line);
                if (why == null && !seen.add(line.split(",", -1)[0])) why = "duplicate pass id in the file";
                if (why == null) valid.add(line.split(",", -1)); else failures.add("line " + lineNo + ": " + why + "  -> " + line);
            }
        }
        try (BufferedWriter w = Files.newBufferedWriter(Path.of("migration_failures.txt"))) { for (String s : failures) { w.write(s); w.newLine(); } }

        System.out.println("Read " + lineNo + " lines: " + valid.size() + " valid, " + failures.size() + " failed validation");
        failures.stream().limit(10).forEach(s -> System.out.println("   FAILED " + s));
        if (failures.size() > 10) System.out.println("   ... (all failures are in migration_failures.txt)");
        if (dry) { System.out.println("--dry-run: nothing was written to the database."); return; }

        long t = System.nanoTime();
        try (Connection con = Db.connect()) {
            Db.createBusPassTable(con);
            int loaded = PassLoader.loadAll(con, valid);                 // all-or-nothing, batches of 1,000
            System.out.printf("Loaded %d rows in %.0f ms in one transaction.%n", loaded, (System.nanoTime() - t) / 1e6);
        } catch (SQLException e) {
            System.out.println("DATABASE ERROR - the transaction was rolled back, no row was loaded: " + e.getMessage());
        }
    }
}
