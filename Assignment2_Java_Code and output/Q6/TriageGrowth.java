import java.util.*;

public class TriageGrowth {
    public static void main(String[] args) {
        for (int n : new int[]{1000, 10000, 100000}) {
            PriorityQueue<long[]> pq = new PriorityQueue<>(
                    (a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
            Random rnd = new Random(2);
            long t = System.nanoTime();
            for (int i = 0; i < n; i++) pq.add(new long[]{1 + rnd.nextInt(5), i});
            double add = (System.nanoTime() - t) / 1e6;
            t = System.nanoTime();
            while (!pq.isEmpty()) pq.poll();
            double poll = (System.nanoTime() - t) / 1e6;
            System.out.printf("n=%-7d add all = %.2f ms   poll all = %.2f ms%n", n, add, poll);
        }
    }
}
