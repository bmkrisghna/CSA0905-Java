/** Alternative concept: cost of capturing a stack trace, and what chaining adds. */
public class StackTraceCost {
    static class Normal extends Exception {
        Normal(String m) { super(m); }
    }
    static class NoTrace extends Exception {
        NoTrace(String m) { super(m); }
        @Override public synchronized Throwable fillInStackTrace() { return this; }   // skip the stack walk
    }

    static int depth(int d, Runnable r) {
        if (d == 0) { r.run(); return 0; }
        return depth(d - 1, r) + 1;
    }

    public static void main(String[] args) {
        int n = 100_000;
        for (int depthLevel : new int[]{5, 50, 200}) {
            // warm-up
            for (int i = 0; i < 20_000; i++) { depth(depthLevel, () -> new Normal("x")); depth(depthLevel, () -> new NoTrace("x")); }

            long t = System.nanoTime();
            for (int i = 0; i < n; i++) depth(depthLevel, () -> new Normal("x"));
            double withTrace = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            for (int i = 0; i < n; i++) depth(depthLevel, () -> new NoTrace("x"));
            double without = (System.nanoTime() - t) / 1e6;

            System.out.printf("stack depth %-4d: %d exceptions with stack trace = %.1f ms | without stack trace = %.1f ms%n",
                    depthLevel, n, withTrace, without);
        }
        Exception inner = new Normal("inner");
        Exception outer = new Exception("outer", inner);
        System.out.println("Chaining: outer holds a reference to inner (cause) -> memory of BOTH exceptions and BOTH stack traces is kept alive.");
        System.out.println("outer.getCause() == inner ? " + (outer.getCause() == inner));
    }
}
