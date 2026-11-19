import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

/**
 * Q3 - Daily sales report. Four versions: no close, leaky manual close, correct finally, try-with-resources.
 * A "Handle" class counts how many files are still open, so leaks can be measured.
 */
public class ReportGenerator {
    static int open = 0;
    static final Logger LOG = Logger.getLogger("report");

    static class Handle extends BufferedReader {
        private boolean closed = false;
        Handle(String path) throws IOException { super(new FileReader(path)); open++; }
        @Override public void close() throws IOException {
            if (!closed) { closed = true; open--; }
            super.close();
        }
    }

    static double parse(String line, String file) {
        try {
            return Double.parseDouble(line.split(",")[1].trim());
        } catch (RuntimeException e) {
            LOG.log(Level.WARNING, "Bad line in file {0}: \"{1}\"", new Object[]{file, line});   // logging: which file failed
            throw new IllegalStateException("Corrupt data in " + file, e);
        }
    }

    // ---- V0: no closing at all ----
    static double noClose(List<String> files) throws IOException {
        double total = 0;
        for (String f : files) {
            Handle h = new Handle(f);
            String line;
            while ((line = h.readLine()) != null) total += parse(line, f);
        }
        return total;
    }

    // ---- V1: manual close(), but close is NOT in finally  (leaks when an exception occurs) ----
    static double manualLeaky(List<String> files) throws IOException {
        double total = 0;
        for (String f : files) {
            Handle h = new Handle(f);
            String line;
            while ((line = h.readLine()) != null) total += parse(line, f);
            h.close();                                        // skipped if parse() throws
        }
        return total;
    }

    // ---- V2: manual close() inside finally (correct but long) ----
    static double manualFinally(List<String> files) throws IOException {
        double total = 0;
        for (String f : files) {
            Handle h = null;
            try {
                h = new Handle(f);
                String line;
                while ((line = h.readLine()) != null) total += parse(line, f);
            } finally {
                if (h != null) {
                    try { h.close(); } catch (IOException e) { LOG.warning("close failed for " + f); }
                }
            }
        }
        return total;
    }

    // ---- V3: try-with-resources ----
    static double tryWithResources(List<String> files) throws IOException {
        double total = 0;
        for (String f : files) {
            try (Handle h = new Handle(f)) {
                String line;
                while ((line = h.readLine()) != null) total += parse(line, f);
            }
        }
        return total;
    }

    interface Version { double run(List<String> files) throws IOException; }

    static void test(String name, Version v, List<String> files) {
        open = 0;
        String outcome;
        try { outcome = String.format("total = %.2f", v.run(files)); }
        catch (Exception e) { outcome = "FAILED -> " + e.getMessage(); }
        System.out.printf("%-18s %-45s files left open = %d%n", name, outcome, open);
    }

    // ---- suppressed exception + closing order demo ----
    static class Res implements AutoCloseable {
        final String name; final boolean failClose;
        Res(String n, boolean f) { name = n; failClose = f; System.out.println("   opened " + n); }
        @Override public void close() throws IOException {
            System.out.println("   closing " + name);
            if (failClose) throw new IOException("close() of " + name + " failed");
        }
    }

    public static void main(String[] args) throws IOException {
        Logger.getLogger("").setLevel(Level.WARNING);
        Path dir = Files.createTempDirectory("sales");
        Path s1 = Files.writeString(dir.resolve("store1.csv"), "pen,10\nbook,20\n");
        Path s2 = Files.writeString(dir.resolve("store2.csv"), "bag,30\nbottle,15\n");
        Path s3 = Files.writeString(dir.resolve("store3_corrupt.csv"), "ruler,5\nBROKEN LINE\n");   // fails part-way
        Path s4 = Files.writeString(dir.resolve("store4.csv"), "cap,25\n");

        List<String> good = List.of(s1.toString(), s2.toString(), s4.toString());
        List<String> bad  = List.of(s1.toString(), s3.toString(), s4.toString());

        System.out.println("=== Same VALID input: all versions must give the same report ===");
        test("noClose",       ReportGenerator::noClose, good);
        test("manualLeaky",   ReportGenerator::manualLeaky, good);
        test("manualFinally", ReportGenerator::manualFinally, good);
        test("tryWithResources", ReportGenerator::tryWithResources, good);

        System.out.println("\n=== Same FAILING input (store3 is corrupt): count open files ===");
        test("noClose",       ReportGenerator::noClose, bad);
        test("manualLeaky",   ReportGenerator::manualLeaky, bad);
        test("manualFinally", ReportGenerator::manualFinally, bad);
        test("tryWithResources", ReportGenerator::tryWithResources, bad);

        // summary file written with try-with-resources
        Path summary = dir.resolve("summary.txt");
        try (BufferedWriter w = Files.newBufferedWriter(summary)) {
            w.write("Total sales = " + tryWithResources(good) + "\n");
        }
        System.out.println("\nSummary file: " + Files.readString(summary).trim());

        System.out.println("\n=== Closing order (reverse of opening) ===");
        try (Res a = new Res("A", false); Res b = new Res("B", false)) {
            System.out.println("   body runs");
        }

        System.out.println("\n=== Suppressed exception ===");
        try (Res r = new Res("R", true)) {
            throw new IllegalStateException("body failed");          // primary exception
        } catch (Exception e) {
            System.out.println("   caught  : " + e.getMessage());
            for (Throwable t : e.getSuppressed()) System.out.println("   suppressed: " + t.getMessage());
        }
    }
}
