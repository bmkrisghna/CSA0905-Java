/** Q4 - four scanner stations: once with "extends Thread", once with "implements Runnable". */
public class StationThreads {
    static int[] batches = {5, 3, 8, 2};                 // scripts per station (x 100 ms of work)
    static final int[] scanned = new int[4];

    static class StationThread extends Thread {          // way 1: extend Thread
        final int id;
        StationThread(int id) { super("Station-" + id); this.id = id; }
        @Override public void run() {
            try { Thread.sleep(batches[id] * 100L); } catch (InterruptedException e) { return; }
            scanned[id] = batches[id] * 10;
            System.out.println(getName() + " finished: " + scanned[id] + " scripts");
        }
    }

    static class StationTask implements Runnable {       // way 2: implement Runnable
        final int id;
        StationTask(int id) { this.id = id; }
        @Override public void run() {
            try { Thread.sleep(batches[id] * 100L); } catch (InterruptedException e) { return; }
            scanned[id] = batches[id] * 10;
            System.out.println(Thread.currentThread().getName() + " finished: " + scanned[id] + " scripts");
        }
    }

    static int total() { int s = 0; for (int x : scanned) s += x; return s; }

    public static void main(String[] args) throws InterruptedException {
        // ---------- way 1 ----------
        System.out.println("== extends Thread ==");
        Thread[] t = new Thread[4];
        for (int i = 0; i < 4; i++) { t[i] = new StationThread(i); t[i].start(); }
        System.out.println("isAlive right after start(): " + t[2].isAlive());
        for (Thread x : t) x.join();                                   // wait for all
        System.out.println("isAlive after join(): " + t[2].isAlive());
        System.out.println("TOTAL scanned = " + total());

        // ---------- way 2 ----------
        java.util.Arrays.fill(scanned, 0);
        System.out.println("\n== implements Runnable ==");
        Thread[] r = new Thread[4];
        for (int i = 0; i < 4; i++) { r[i] = new Thread(new StationTask(i), "Runner-" + i); r[i].start(); }
        for (Thread x : r) x.join();
        System.out.println("TOTAL scanned = " + total());
    }
}
