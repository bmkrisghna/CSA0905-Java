/** Same counter but "while" changed to "if". Runs 200 times with 4 consumers and counts wrong results. */
public class TokenCounterIfBug {
    static class Counter {
        final int[] buffer = new int[5]; int count = 0, in = 0, out = 0, minSeen = 0, maxSeen = 0;
        final boolean useWhile;
        Counter(boolean useWhile) { this.useWhile = useWhile; }

        synchronized void put(int token) throws InterruptedException {
            if (useWhile) { while (count == 5) wait(); } else { if (count == 5) wait(); }
            buffer[in] = token; in = (in + 1) % 5; count++; maxSeen = Math.max(maxSeen, count); notifyAll();
        }
        synchronized int take() throws InterruptedException {
            if (useWhile) { while (count == 0) wait(); } else { if (count == 0) wait(); }
            int t = buffer[out]; out = (out + 1) % 5; count--; minSeen = Math.min(minSeen, count); notifyAll(); return t;
        }
    }

    /** returns "OK", "WRONG SUM", or "HUNG" */
    static String runOnce(boolean useWhile) throws Exception {
        Counter c = new Counter(useWhile);
        int consumers = 4, each = 5;                               // 4 x 5 = 20 tokens
        int[] got = new int[consumers];
        Thread cook = new Thread(() -> { try { for (int t = 1; t <= 20; t++) c.put(t); } catch (InterruptedException e) { } });
        cook.setDaemon(true);
        Thread[] st = new Thread[consumers];
        for (int s = 0; s < consumers; s++) {
            final int id = s;
            st[s] = new Thread(() -> { try { for (int k = 0; k < each; k++) got[id] += c.take(); } catch (InterruptedException e) { } });
            st[s].setDaemon(true);
        }
        cook.start(); for (Thread t : st) t.start();
        cook.join(2000);
        for (Thread t : st) t.join(2000);
        for (Thread t : st) if (t.isAlive()) return "HUNG";
        int sum = 0; for (int g : got) sum += g;
        if (c.minSeen < 0) return "WRONG SUM (counter went below 0)";
        return sum == 210 ? "OK" : "WRONG SUM (" + sum + ")";
    }

    public static void main(String[] args) throws Exception {
        for (boolean useWhile : new boolean[]{true, false}) {
            int ok = 0, wrong = 0, hung = 0;
            for (int i = 0; i < 200; i++) {
                String r = runOnce(useWhile);
                if (r.equals("OK")) ok++; else if (r.equals("HUNG")) hung++; else wrong++;
            }
            System.out.println((useWhile ? "while" : "if   ") + " version: OK=" + ok + "  WRONG=" + wrong + "  HUNG=" + hung + "   (200 runs)");
        }
    }
}
