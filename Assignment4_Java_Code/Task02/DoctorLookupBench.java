import java.util.*;

/**
 * Alternative concept: load doctors once into a Map (O(1) per change) vs fetch from the database on every change.
 * The database is simulated with a fixed delay per query (default 2 ms = a typical local round trip).
 */
public class DoctorLookupBench {
    static final int QUERY_DELAY_MICROS = 2000;

    static List<String> fetchFromDatabase(String dept, Map<String, List<String>> truth) {
        try { Thread.sleep(QUERY_DELAY_MICROS / 1000, (QUERY_DELAY_MICROS % 1000) * 1000); } catch (InterruptedException e) { }
        return new ArrayList<>(truth.get(dept));          // copy = new result set each time
    }

    public static void main(String[] args) {
        for (int d : new int[]{10, 100, 1000}) {
            int m = 20;                                   // doctors per department
            Map<String, List<String>> map = new HashMap<>();
            List<String> depts = new ArrayList<>();
            for (int i = 0; i < d; i++) {
                String dept = "Dept" + i; depts.add(dept);
                List<String> docs = new ArrayList<>();
                for (int j = 0; j < m; j++) docs.add("Dr" + i + "_" + j);
                map.put(dept, docs);
            }
            Random rnd = new Random(1);
            int changes = 300;
            long sink = 0;

            long t = System.nanoTime();
            for (int i = 0; i < changes; i++) sink += map.get(depts.get(rnd.nextInt(d))).size();
            double mapMs = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            for (int i = 0; i < changes; i++) sink += fetchFromDatabase(depts.get(rnd.nextInt(d)), map).size();
            double dbMs = (System.nanoTime() - t) / 1e6;

            System.out.printf("d=%-5d m=%d | %d department changes: Map = %.3f ms total (%.5f ms each) | database fetch = %.1f ms total (%.2f ms each) | Map stores d*m = %d names (sink %d)%n",
                    d, m, changes, mapMs, mapMs / changes, dbMs, dbMs / changes, d * m, sink);
        }
    }
}
