/** Student A: record the counter size over time - it must stay between 0 and 5. */
public class MessCounterTrace {
    private final int[] buffer = new int[5];
    private int count = 0, in = 0, out = 0;
    private int minSeen = 0, maxSeen = 0;

    synchronized void put(int token) throws InterruptedException {
        while (count == 5) wait();
        buffer[in] = token; in = (in + 1) % 5; count++;
        maxSeen = Math.max(maxSeen, count);
        System.out.println("  put " + token + "  -> size " + count);
        notifyAll();
    }

    synchronized int take(String who) throws InterruptedException {
        while (count == 0) wait();
        int t = buffer[out]; out = (out + 1) % 5; count--;
        minSeen = Math.min(minSeen, count);
        System.out.println("  " + who + " took " + t + " -> size " + count);
        notifyAll();
        return t;
    }

    public static void main(String[] args) throws Exception {
        MessCounterTrace c = new MessCounterTrace();
        Thread cook = new Thread(() -> { try { for (int t = 1; t <= 12; t++) c.put(t); } catch (InterruptedException e) { } });
        Thread s1 = new Thread(() -> { try { for (int k = 0; k < 6; k++) { c.take("Student1"); Thread.sleep(20); } } catch (InterruptedException e) { } });
        Thread s2 = new Thread(() -> { try { for (int k = 0; k < 6; k++) { c.take("Student2"); Thread.sleep(35); } } catch (InterruptedException e) { } });
        cook.start(); s1.start(); s2.start();
        cook.join(); s1.join(); s2.join();
        System.out.println("Smallest size seen = " + c.minSeen + ", largest size seen = " + c.maxSeen + "  (must be within 0..5)");
    }
}
