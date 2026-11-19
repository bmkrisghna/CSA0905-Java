import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Alternative concept for B4.3: each of the 12 campuses keeps its OWN serialized file.
 *  Part 1: searching "all passes" means opening and deserializing 12 files (time and memory).
 *  Part 2: two counters at the same campus read-modify-write the same file at the same time -> lost updates.
 *   java CampusFilesAlternative 120000
 */
public class CampusFilesAlternative {
    static class Pass implements Serializable {
        private static final long serialVersionUID = 1L;
        String id, name; int route;
        Pass(String id, String name, int route) { this.id = id; this.name = name; this.route = route; }
    }

    @SuppressWarnings("unchecked")
    static ArrayList<Pass> load(Path f) throws Exception {
        if (!Files.exists(f)) return new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(f)))) { return (ArrayList<Pass>) in.readObject(); }
    }
    static void save(Path f, ArrayList<Pass> l) throws Exception {
        try (ObjectOutputStream o = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(f)))) { o.writeObject(l); }
    }

    public static void main(String[] args) throws Exception {
        int total = args.length > 0 ? Integer.parseInt(args[0]) : 120_000;
        int campuses = 12, perCampus = total / campuses;
        Path dir = Files.createTempDirectory("campuses");
        Path[] files = new Path[campuses];
        for (int c = 0; c < campuses; c++) {
            files[c] = dir.resolve("campus" + c + ".ser");
            ArrayList<Pass> l = new ArrayList<>();
            for (int i = 0; i < perCampus; i++) l.add(new Pass("C" + c + "-" + i, "Student" + i, 1 + i % 40));
            save(files[c], l);
        }

        String wanted = "C11-" + (perCampus - 1);
        for (int w = 0; w < 2; w++) {
            long t = System.nanoTime(); boolean found = false; long bytes = 0;
            for (Path f : files) { bytes += Files.size(f); for (Pass p : load(f)) if (p.id.equals(wanted)) found = true; }
            if (w == 1) System.out.printf("Part 1: search %d passes in %d separate files: %.0f ms (found=%b), %d KB read from disk, each file must be fully loaded (O(n) time, O(n/12) memory at a time)%n",
                    total, campuses, (System.nanoTime() - t) / 1e6, found, bytes / 1024);
        }

        // ---- Part 2: concurrent counters on ONE campus file (no locking) ----
        Path shared = dir.resolve("shared.ser");
        save(shared, new ArrayList<>());
        int perCounter = 100;
        AtomicInteger done = new AtomicInteger();
        Thread[] counters = new Thread[4];
        for (int k = 0; k < counters.length; k++) {
            final int id = k;
            counters[k] = new Thread(() -> {
                try {
                    for (int i = 0; i < perCounter; i++) {
                        ArrayList<Pass> l = load(shared);                  // read ...
                        l.add(new Pass("K" + id + "-" + i, "X", 1));       // ... modify ...
                        save(shared, l);                                   // ... write back (another counter may have written in between)
                        done.incrementAndGet();
                    }
                } catch (Exception e) { done.incrementAndGet(); }          // a reader may even see a half-written file
            });
            counters[k].start();
        }
        for (Thread t : counters) t.join();
        int stored = 0;
        try { stored = load(shared).size(); } catch (Exception e) { System.out.println("Part 2: the file is CORRUPT: " + e); }
        System.out.printf("Part 2: 4 counters issued %d passes, the file contains %d  -> %d passes LOST (a database transaction would keep all %d)%n",
                4 * perCounter, stored, 4 * perCounter - stored, 4 * perCounter);
        try (var s = Files.walk(dir)) { s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
    }
}
