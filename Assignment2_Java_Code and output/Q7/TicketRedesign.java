/** Q7: Original ('+' concatenation) vs Redesign (one reused, pre-sized StringBuilder). */
public class TicketRedesign {
    static final int TICKETS = 10000, FIELDS = 50, RUNS = 7;

    public static void main(String[] args) {
        double orig = 0, newSb = 0, reuse = 0;
        String a = null, b = null, c = null;
        for (int run = 0; run < RUNS; run++) {
            long t = System.nanoTime();
            for (int i = 0; i < TICKETS; i++) {
                String q = "";
                for (int f = 0; f < FIELDS; f++) q += "field" + f + ";";
                a = q;
            }
            double t1 = ms(t);

            t = System.nanoTime();
            for (int i = 0; i < TICKETS; i++) {
                StringBuilder q = new StringBuilder();
                for (int f = 0; f < FIELDS; f++) q.append("field").append(f).append(';');
                b = q.toString();
            }
            double t2 = ms(t);

            StringBuilder sb = new StringBuilder(600);          // created ONCE, capacity set in advance
            t = System.nanoTime();
            for (int i = 0; i < TICKETS; i++) {
                sb.setLength(0);                                // reuse
                for (int f = 0; f < FIELDS; f++) sb.append("field").append(f).append(';');
                c = sb.toString();
            }
            double t3 = ms(t);

            if (run >= 2) { orig += t1; newSb += t2; reuse += t3; }     // first 2 runs = warm-up
        }
        int n = RUNS - 2;
        System.out.printf("Original (+)                    : %.2f ms%n", orig / n);
        System.out.printf("New StringBuilder per ticket    : %.2f ms%n", newSb / n);
        System.out.printf("Reused, pre-sized StringBuilder : %.2f ms%n", reuse / n);
        System.out.println("Same text produced by all three? " + (a.equals(b) && b.equals(c)));
        System.out.printf("Speed-up original -> redesign   : %.1fx%n", orig / reuse);
    }

    static double ms(long t) { return (System.nanoTime() - t) / 1e6; }
}
