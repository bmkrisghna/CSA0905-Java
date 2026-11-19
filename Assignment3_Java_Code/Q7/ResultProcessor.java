import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;
import java.util.concurrent.locks.LockSupport;

/**
 * Q7 - Semester result processing: 10,000 score files, some corrupt, some missing.
 * Design: ExecutorService + Callable<Result> + Future, ConcurrentHashMap for results, AtomicInteger counters.
 * One bad file never stops the run: each task catches its own failures and returns a FAILED result.
 */
public class ResultProcessor {

    // ---------------- result object ----------------
    enum Status { OK, CORRUPT, MISSING }

    static class Result {
        final String file; final Status status; final double average; final String reason;
        Result(String file, Status status, double average, String reason) {
            this.file = file; this.status = status; this.average = average; this.reason = reason;
        }
    }

    static final int IO_DELAY_MICROS = 100;          // simulated disk/network read time per file

    // ---------------- the work for ONE file ----------------
    static Result process(Path path) {
        String name = path.getFileName().toString();
        try {
            LockSupport.parkNanos(IO_DELAY_MICROS * 1000L);            // simulated I/O wait
            String line = Files.readString(path).trim();               // NoSuchFileException if missing
            String[] parts = line.split(",");
            if (parts.length != 4) throw new IllegalArgumentException("expected 4 fields but found " + parts.length);
            double sum = 0;
            for (int i = 1; i < 4; i++) {
                double mark = Double.parseDouble(parts[i].trim());    // NumberFormatException if corrupt
                if (mark < 0 || mark > 100) throw new IllegalArgumentException("mark out of range: " + mark);
                sum += mark;
            }
            return new Result(name, Status.OK, sum / 3, "");
        } catch (NoSuchFileException e) {
            return new Result(name, Status.MISSING, 0, "file not found");
        } catch (IOException e) {
            return new Result(name, Status.MISSING, 0, "I/O error: " + e.getMessage());
        } catch (RuntimeException e) {                                 // NumberFormat, IllegalArgument ...
            return new Result(name, Status.CORRUPT, 0, e.getMessage());
        }
    }

    // ---------------- summary (thread-safe collections) ----------------
    static class Summary {
        final ConcurrentHashMap<String, Double> averages = new ConcurrentHashMap<>();   // shared results
        final ConcurrentLinkedQueue<String> failures = new ConcurrentLinkedQueue<>();   // shared failure log
        final AtomicInteger ok = new AtomicInteger(), corrupt = new AtomicInteger(),
                            missing = new AtomicInteger(), unexpected = new AtomicInteger();

        void add(Result r) {
            switch (r.status) {
                case OK:      ok.incrementAndGet(); averages.put(r.file, r.average); break;
                case CORRUPT: corrupt.incrementAndGet(); failures.add(r.file + " : CORRUPT : " + r.reason); break;
                default:      missing.incrementAndGet(); failures.add(r.file + " : MISSING : " + r.reason);
            }
        }
        int total() { return ok.get() + corrupt.get() + missing.get() + unexpected.get(); }
        @Override public String toString() {
            return "OK=" + ok + " CORRUPT=" + corrupt + " MISSING=" + missing + " UNEXPECTED=" + unexpected + " TOTAL=" + total();
        }
    }

    // ---------------- ExecutorService + Callable + Future ----------------
    static Summary runWithPool(List<Path> files, int threads) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<Result>> futures = new ArrayList<>();
        for (Path p : files) {
            Callable<Result> task = () -> process(p);
            futures.add(pool.submit(task));
        }
        Summary s = new Summary();
        for (Future<Result> f : futures) {
            try {
                s.add(f.get());                                       // blocks until that task is done
            } catch (ExecutionException e) {                          // a task failed in an UNEXPECTED way
                s.unexpected.incrementAndGet();
                s.failures.add("UNEXPECTED : " + e.getCause());
            }
        }
        pool.shutdown();
        return s;
    }

    // ---------------- alternatives ----------------
    static Summary runParallelStream(List<Path> files) {
        Summary s = new Summary();
        files.parallelStream().forEach(p -> s.add(process(p)));       // uses the shared ForkJoin common pool
        return s;
    }

    static Summary runThreadPerFile(List<Path> files) throws InterruptedException {
        Summary s = new Summary();
        List<Thread> threads = new ArrayList<>();
        for (Path p : files) {
            Thread t = new Thread(() -> s.add(process(p)));
            threads.add(t);
            t.start();                                                // one thread per file: 10,000 threads!
        }
        for (Thread t : threads) t.join();
        return s;
    }

    // ---------------- test data ----------------
    static List<Path> makeFiles(Path dir, int n) throws IOException {
        List<Path> list = new ArrayList<>();
        Random r = new Random(7);
        for (int i = 0; i < n; i++) {
            Path p = dir.resolve("score" + i + ".txt");
            int kind = r.nextInt(100);
            if (kind < 3) {                                           // 3% corrupt (non numeric)
                Files.writeString(p, "S" + i + ",xx," + r.nextInt(100) + "," + r.nextInt(100));
            } else if (kind < 5) {                                    // 2% corrupt (out of range)
                Files.writeString(p, "S" + i + ",150,60,70");
            } else if (kind < 7) {
                // 2% MISSING: file is simply not created
            } else {
                Files.writeString(p, "S" + i + "," + r.nextInt(101) + "," + r.nextInt(101) + "," + r.nextInt(101));
            }
            list.add(p);
        }
        return list;
    }

    // ---------------- main ----------------
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("results");

        // ===== Student B: hand-checked small set =====
        System.out.println("=== Small hand-checked set (6 files) ===");
        List<Path> small = new ArrayList<>();
        small.add(Files.writeString(dir.resolve("a.txt"), "S1,60,70,80"));      // average 70
        small.add(Files.writeString(dir.resolve("b.txt"), "S2,90,90,90"));      // average 90
        small.add(Files.writeString(dir.resolve("c.txt"), "S3,50,50,50"));      // average 50
        small.add(Files.writeString(dir.resolve("d.txt"), "S4,abc,70,80"));     // CORRUPT (not a number)
        small.add(Files.writeString(dir.resolve("e.txt"), "S5,120,70,80"));     // CORRUPT (out of range)
        small.add(dir.resolve("missing.txt"));                                  // MISSING
        Summary hs = runWithPool(small, 2);
        System.out.println("Expected : OK=3 CORRUPT=2 MISSING=1 TOTAL=6, averages 70, 90, 50");
        System.out.println("Actual   : " + hs);
        System.out.println("Averages : a=" + hs.averages.get("a.txt") + " b=" + hs.averages.get("b.txt") + " c=" + hs.averages.get("c.txt"));
        boolean pass = hs.ok.get() == 3 && hs.corrupt.get() == 2 && hs.missing.get() == 1 && hs.total() == 6
                && hs.averages.get("a.txt") == 70.0 && hs.averages.get("b.txt") == 90.0 && hs.averages.get("c.txt") == 50.0;
        System.out.println("Hand check: " + (pass ? "PASS" : "FAIL"));
        System.out.println("Failure log:"); hs.failures.forEach(f -> System.out.println("   " + f));

        // ===== Student B: 10,000 files, pools of 1, 2, 4, 8 =====
        int n = 10_000;
        List<Path> files = makeFiles(dir, n);
        System.out.println("\n=== " + n + " files (about 3% non-numeric, 2% out of range, 2% missing), simulated read time "
                + IO_DELAY_MICROS + " microseconds per file ===");
        runWithPool(files.subList(0, 500), 4);                                   // warm-up
        for (int threads : new int[]{1, 2, 4, 8}) {
            long t = System.nanoTime();
            Summary s = runWithPool(files, threads);
            double ms = (System.nanoTime() - t) / 1e6;
            System.out.printf("pool of %d threads : %8.1f ms   %s%n", threads, ms, s);
        }

        // ===== Alternative concepts =====
        System.out.println("\n=== Alternative concepts ===");
        long t = System.nanoTime();
        Summary ps = runParallelStream(files);
        System.out.printf("parallelStream     : %8.1f ms   %s  (common pool size = %d)%n",
                (System.nanoTime() - t) / 1e6, ps, ForkJoinPool.commonPool().getParallelism());

        t = System.nanoTime();
        Summary tp = runThreadPerFile(files);
        System.out.printf("one thread per file: %8.1f ms   %s  (10,000 threads created)%n", (System.nanoTime() - t) / 1e6, tp);

        System.out.println("\nCPU cores available: " + Runtime.getRuntime().availableProcessors());
    }
}
