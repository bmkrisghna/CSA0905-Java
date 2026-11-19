import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PassStore {
    private final Path file;
    PassStore(Path file) { this.file = file; }

    public void add(String passId, String regNo, String name, int route, double amount)
            throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(String.join(",", passId, regNo, name,
                    String.valueOf(route), String.valueOf(amount)));
            w.newLine();
        }
    }

    /** A line is damaged if it does not have exactly 5 fields, or route / amount are not numbers. */
    static boolean isDamaged(String[] f) {
        if (f.length != 5) return true;
        try { Integer.parseInt(f[3].trim()); Double.parseDouble(f[4].trim()); return false; }
        catch (NumberFormatException e) { return true; }
    }

    /** Returns the five fields of the pass with the given ID, or null if not found. */
    public String[] find(String passId) throws IOException {
        // TODO 1 (done): read line by line with a BufferedReader inside try-with-resources;
        //   skip damaged lines; return the fields of the matching pass
        if (!Files.exists(file)) return null;                               // missing file -> nothing found
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (isDamaged(f)) continue;                                  // damaged / empty line: skip, do not stop
                if (f[0].equals(passId)) return f;
            }
        }
        return null;
    }

    /** List all valid passes on a route (operation iii of Task A1). */
    public List<String[]> listByRoute(int route) throws IOException {
        List<String[]> out = new ArrayList<>();
        if (!Files.exists(file)) return out;
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] f = line.split(",", -1);
                if (!isDamaged(f) && Integer.parseInt(f[3].trim()) == route) out.add(f);
            }
        }
        return out;
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("passes", ".csv");
        PassStore store = new PassStore(f);
        store.add("P1001", "192311001", "Arun", 12, 4500);
        Files.writeString(f, "P1002,DAMAGED LINE\n", StandardOpenOption.APPEND);
        store.add("P1003", "192311003", "Divya", 7, 3800);
        System.out.println(Arrays.toString(store.find("P1003")));
        System.out.println(Arrays.toString(store.find("P1002")));
        Files.delete(f);
    }
}
