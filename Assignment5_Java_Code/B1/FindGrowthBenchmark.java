import java.io.*;
import java.nio.file.*;
import java.util.*;

/** B1.4 - time to find a pass as the file grows. Also the alternative: read once into a HashMap. */
public class FindGrowthBenchmark {
    public static void main(String[] args) throws Exception {
        int q = 100;                                                 // number of searches
        for (int n : new int[]{1_000, 10_000, 100_000}) {
            Path f = Files.createTempFile("passes", ".csv");
            try (BufferedWriter w = Files.newBufferedWriter(f)) {
                for (int i = 0; i < n; i++) { w.write("P" + (1001 + i) + "," + String.format("%09d", i) + ",Student" + i + "," + (1 + i % 40) + "," + (3000 + i % 2000) + ".0"); w.newLine(); }
            }
            PassStore store = new PassStore(f);
            String last = "P" + (1001 + n - 1);                     // worst case: last record
            store.find(last); store.find(last);                      // warm-up

            long t = System.nanoTime();
            for (int i = 0; i < q; i++) store.find(last);
            double scanOne = (System.nanoTime() - t) / 1e6 / q;

            t = System.nanoTime();
            Map<String, String[]> map = new HashMap<>();
            try (BufferedReader r = Files.newBufferedReader(f)) { String line; while ((line = r.readLine()) != null) { String[] p = line.split(","); map.put(p[0], p); } }
            double load = (System.nanoTime() - t) / 1e6;
            t = System.nanoTime();
            long sink = 0;
            for (int i = 0; i < q; i++) sink += map.get(last).length;
            double lookups = (System.nanoTime() - t) / 1e6;

            System.out.printf("n=%-7d scan one search = %.2f ms | %d searches by scanning = %.1f ms | HashMap: load once = %.1f ms + %d lookups = %.3f ms (sink %d)%n",
                    n, scanOne, q, scanOne * q, load, q, lookups, sink);
            Files.delete(f);
        }
        System.out.println("Scanning: O(q x n) time, O(1) memory. HashMap: O(n + q) time, O(n) memory.");
    }
}
