import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Q5 - reliable race reproduction + comparison of synchronization strategies.
 * Every version books seat (i % SEATS) for 10,000 requests, so the correct answer is exactly SEATS bookings.
 */
public class BookingRace {
    static final int SEATS = 100, REQUESTS = 10_000, THREADS = 8;

    interface Booker { void book(int seat); int sold(); }

    static int dummyWork(int seat) {                       // non-critical work (e.g. building the ticket text)
        int h = seat;
        for (int i = 0; i < 2000; i++) h = h * 31 + i;
        return h;
    }

    // 1. no synchronization (with Thread.yield() to widen the window between check and book)
    static class Unsafe implements Booker {
        final boolean[] booked = new boolean[SEATS]; int sold = 0;
        public void book(int seat) {
            if (!booked[seat]) { Thread.yield(); booked[seat] = true; sold++; }
        }
        public int sold() { return sold; }
    }
    // 2. synchronized METHOD: lock held during the dummy work too
    static class SyncMethod implements Booker {
        final boolean[] booked = new boolean[SEATS]; int sold = 0;
        public synchronized void book(int seat) {
            dummyWork(seat);
            if (!booked[seat]) { booked[seat] = true; sold++; }
        }
        public int sold() { return sold; }
    }
    // 3. synchronized BLOCK: lock only the critical section
    static class SyncBlock implements Booker {
        final boolean[] booked = new boolean[SEATS]; int sold = 0;
        public void book(int seat) {
            dummyWork(seat);                                // outside the lock
            synchronized (this) {                           // intrinsic lock of this object
                if (!booked[seat]) { booked[seat] = true; sold++; }
            }
        }
        public int sold() { return sold; }
    }
    // 4. AtomicBoolean + compareAndSet (lock-free)
    static class AtomicSeats implements Booker {
        final AtomicBoolean[] booked = new AtomicBoolean[SEATS]; final AtomicInteger sold = new AtomicInteger();
        AtomicSeats() { for (int i = 0; i < SEATS; i++) booked[i] = new AtomicBoolean(); }
        public void book(int seat) {
            dummyWork(seat);
            if (booked[seat].compareAndSet(false, true)) sold.incrementAndGet();
        }
        public int sold() { return sold.get(); }
    }
    // 5. ReentrantLock
    static class LockSeats implements Booker {
        final boolean[] booked = new boolean[SEATS]; int sold = 0; final ReentrantLock lock = new ReentrantLock();
        public void book(int seat) {
            dummyWork(seat);
            lock.lock();
            try { if (!booked[seat]) { booked[seat] = true; sold++; } } finally { lock.unlock(); }
        }
        public int sold() { return sold; }
    }
    // 6. ConcurrentHashMap.putIfAbsent
    static class MapSeats implements Booker {
        final ConcurrentHashMap<Integer, Boolean> map = new ConcurrentHashMap<>(); final AtomicInteger sold = new AtomicInteger();
        public void book(int seat) {
            dummyWork(seat);
            if (map.putIfAbsent(seat, Boolean.TRUE) == null) sold.incrementAndGet();
        }
        public int sold() { return sold.get(); }
    }

    static double runOnce(Booker b) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        long t = System.nanoTime();
        for (int i = 0; i < REQUESTS; i++) { final int seat = i % SEATS; pool.execute(() -> b.book(seat)); }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
        return (System.nanoTime() - t) / 1e6;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("== Student A: reproduce double booking (20 runs, correct answer = " + SEATS + ") ==");
        int runsWithRace = 0, extra = 0;
        for (int r = 1; r <= 20; r++) {
            Unsafe u = new Unsafe();
            runOnce(u);
            int doubleBooked = u.sold() - SEATS;
            if (doubleBooked != 0) runsWithRace++;
            extra += Math.max(0, doubleBooked);
            if (r <= 5) System.out.println("   run " + r + ": sold = " + u.sold() + "  (extra = " + doubleBooked + ")");
        }
        System.out.println("   runs with a race: " + runsWithRace + " / 20, total extra bookings = " + extra);

        System.out.println("\n== Student B: time for " + REQUESTS + " bookings with " + THREADS + " threads (average of 5 runs) ==");
        String[] names = {"synchronized method", "synchronized block", "AtomicBoolean CAS", "ReentrantLock", "ConcurrentHashMap"};
        for (int k = 0; k < names.length; k++) {
            runOnce(make(k));                                   // warm-up
            double sum = 0; int lastSold = 0;
            for (int r = 0; r < 5; r++) { Booker b = make(k); sum += runOnce(b); lastSold = b.sold(); }
            System.out.printf("   %-22s %8.2f ms   sold = %d  %s%n", names[k], sum / 5, lastSold, lastSold == SEATS ? "(correct)" : "(WRONG)");
        }
    }

    static Booker make(int k) {
        switch (k) {
            case 0: return new SyncMethod();
            case 1: return new SyncBlock();
            case 2: return new AtomicSeats();
            case 3: return new LockSeats();
            default: return new MapSeats();
        }
    }
}
