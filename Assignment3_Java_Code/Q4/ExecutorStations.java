import java.util.*;
import java.util.concurrent.*;

/** Alternative concept: fixed thread pool instead of creating Thread objects yourself. */
public class ExecutorStations {
    public static void main(String[] args) throws Exception {
        int stations = 8;                                     // more tasks than threads
        ExecutorService pool = Executors.newFixedThreadPool(4);
        long start = System.nanoTime();
        List<Future<Integer>> results = new ArrayList<>();
        for (int i = 0; i < stations; i++) {
            final int id = i;
            results.add(pool.submit(() -> {
                Thread.sleep(200);                            // simulated scanning work
                System.out.println("Task " + id + " ran on " + Thread.currentThread().getName());
                return 10;
            }));
        }
        int total = 0;
        for (Future<Integer> f : results) total += f.get();   // get() waits like join()
        pool.shutdown();
        System.out.printf("TOTAL scanned = %d in %.0f ms (8 tasks x 200 ms on 4 threads = about 2 batches = 400 ms)%n",
                total, (System.nanoTime() - start) / 1e6);
    }
}
