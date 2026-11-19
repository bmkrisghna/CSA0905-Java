import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/** Alternatives: ArrayBlockingQueue instead of wait/notify, tryLock with timeout instead of lock ordering. */
public class AlternativeDesigns {

    static class Acct {
        final ReentrantLock lock = new ReentrantLock(); int balance = 1000;
    }

    /** returns true if the transfer happened, false if it could not get both locks in time */
    static boolean transfer(Acct from, Acct to, int amount) throws InterruptedException {
        if (from.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
            try {
                if (to.lock.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try { from.balance -= amount; to.balance += amount; return true; }
                    finally { to.lock.unlock(); }
                }
            } finally { from.lock.unlock(); }
        }
        return false;                                  // give up (caller may retry) - no deadlock
    }

    public static void main(String[] args) throws Exception {
        // ---- Part B alternative: ArrayBlockingQueue ----
        BlockingQueue<Integer> counter = new ArrayBlockingQueue<>(5);
        int[] collected = new int[2];
        Thread cook = new Thread(() -> { try { for (int t = 1; t <= 20; t++) counter.put(t); } catch (InterruptedException e) { } });
        Thread[] students = new Thread[2];
        for (int s = 0; s < 2; s++) {
            final int id = s;
            students[s] = new Thread(() -> { try { for (int k = 0; k < 10; k++) collected[id] += counter.take(); } catch (InterruptedException e) { } });
        }
        cook.start(); for (Thread t : students) t.start();
        cook.join(); for (Thread t : students) t.join();
        System.out.println("ArrayBlockingQueue: sum of tokens collected = " + (collected[0] + collected[1]) + " (expected 210)");

        // ---- Part A alternative: tryLock with timeout, opposite transfers ----
        Acct a = new Acct(), b = new Acct();
        boolean[] done = new boolean[2];
        Thread t1 = new Thread(() -> { try { done[0] = transfer(a, b, 100); } catch (InterruptedException e) { } });
        Thread t2 = new Thread(() -> { try { done[1] = transfer(b, a, 50); }  catch (InterruptedException e) { } });
        t1.start(); t2.start(); t1.join(); t2.join();
        System.out.println("tryLock transfers: A->B " + (done[0] ? "done" : "gave up") + ", B->A " + (done[1] ? "done" : "gave up")
                + "  (program finished, so there is no deadlock)");

        // ---- timing: 1,000,000 put/take pairs ----
        int n = 1_000_000;
        BlockingQueue<Integer> q = new ArrayBlockingQueue<>(5);
        Thread prod = new Thread(() -> { try { for (int i = 0; i < n; i++) q.put(i); } catch (InterruptedException e) { } });
        long t = System.nanoTime();
        prod.start();
        for (int i = 0; i < n; i++) q.take();
        prod.join();
        System.out.printf("ArrayBlockingQueue: %d put/take pairs = %.0f ms%n", n, (System.nanoTime() - t) / 1e6);

        TokenCounter tc = new TokenCounter();
        Thread prod2 = new Thread(() -> { try { for (int i = 0; i < n; i++) tc.put(i); } catch (InterruptedException e) { } });
        t = System.nanoTime();
        prod2.start();
        for (int i = 0; i < n; i++) tc.take();
        prod2.join();
        System.out.printf("wait/notifyAll    : %d put/take pairs = %.0f ms%n", n, (System.nanoTime() - t) / 1e6);
    }

    static class TokenCounter {
        private final int[] buffer = new int[5]; private int count = 0, in = 0, out = 0;
        synchronized void put(int x) throws InterruptedException {
            while (count == 5) wait();
            buffer[in] = x; in = (in + 1) % 5; count++; notifyAll();
        }
        synchronized int take() throws InterruptedException {
            while (count == 0) wait();
            int x = buffer[out]; out = (out + 1) % 5; count--; notifyAll(); return x;
        }
    }
}
