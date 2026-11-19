import java.util.*;

/** Fair rule (aging): a patient who has waited 'LIMIT' time units becomes 1 level more urgent. */
public class AgingTriage {
    static final int LIMIT = 2;

    static class P {
        String name; int severity; long arrival; long waitSince;
        P(String n, int s, long a) { name = n; severity = s; arrival = a; waitSince = a; }
        @Override public String toString() { return name + "(S" + severity + ")"; }
    }

    public static void main(String[] args) {
        Comparator<P> cmp = Comparator.comparingInt((P p) -> p.severity).thenComparingLong(p -> p.arrival);
        PriorityQueue<P> q = new PriorityQueue<>(cmp);
        q.add(new P("Asha", 5, 0));                         // minor patient arrives first
        for (long now = 1; now <= 14; now++) {
            q.add(new P("Crit" + now, 1, now));             // a critical patient arrives every time unit
            List<P> waiting = new ArrayList<>(q);
            for (P p : waiting) {
                if (now - p.waitSince >= LIMIT && p.severity > 1) {   // aging step
                    q.remove(p);
                    p.severity--;
                    p.waitSince = now;
                    q.add(p);                               // re-insert so the heap is rebuilt correctly
                }
            }
            P treated = q.poll();                           // doctor treats one patient per time unit
            System.out.println("t=" + now + " treat " + treated);
        }
    }
}
