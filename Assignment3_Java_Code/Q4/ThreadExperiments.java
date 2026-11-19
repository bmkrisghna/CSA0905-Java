/** Student B experiments: priorities and daemon threads. Run it several times. */
public class ThreadExperiments {
    static volatile boolean stop = false;

    static Thread counter(String name, int priority, long[] result, int idx) {
        Thread t = new Thread(() -> {
            long c = 0;
            while (!stop) c++;
            result[idx] = c;
        }, name);
        t.setPriority(priority);
        return t;
    }

    public static void main(String[] args) throws Exception {
        // ---- priorities ----
        long[] counts = new long[3];
        Thread low  = counter("low",  Thread.MIN_PRIORITY,  counts, 0);
        Thread norm = counter("norm", Thread.NORM_PRIORITY, counts, 1);
        Thread high = counter("high", Thread.MAX_PRIORITY,  counts, 2);
        low.start(); norm.start(); high.start();
        Thread.sleep(1000);
        stop = true;
        low.join(); norm.join(); high.join();
        System.out.println("Loop counts in 1 second (priority is only a hint to the scheduler):");
        System.out.println("   MIN priority  : " + counts[0]);
        System.out.println("   NORM priority : " + counts[1]);
        System.out.println("   MAX priority  : " + counts[2]);

        // ---- daemon thread ----
        Thread normalStation = new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) { }
            System.out.println("User thread finished (JVM waited for it)");
        });
        Thread daemonStation = new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException e) { }
            System.out.println("Daemon thread finished  <-- you will NEVER see this line");
        });
        daemonStation.setDaemon(true);
        normalStation.start();
        daemonStation.start();
        System.out.println("main() ends now. isDaemon = " + daemonStation.isDaemon());
    }
}
