public class StringBenchmark {
    static final int RUNS = 5;

    public static void main(String[] args) {
        int[][] workloads = {{1, 10}, {1000, 50}, {10000, 50}};   // {tickets, fields}
        warmUp();
        System.out.println("Average of " + RUNS + " runs (ms)");
        for (int[] w : workloads) {
            int tickets = w[0], fields = w[1];
            double s = 0, sb = 0, sbuf = 0;
            for (int r = 0; r < RUNS; r++) {
                long t = System.nanoTime();
                for (int i = 0; i < tickets; i++) {
                    String q = "";
                    for (int f = 0; f < fields; f++) q += "field" + f + ";";
                }
                s += ms(t);

                t = System.nanoTime();
                for (int i = 0; i < tickets; i++) {
                    StringBuilder q = new StringBuilder();
                    for (int f = 0; f < fields; f++) q.append("field").append(f).append(';');
                    q.toString();
                }
                sb += ms(t);

                t = System.nanoTime();
                for (int i = 0; i < tickets; i++) {
                    StringBuffer q = new StringBuffer();
                    for (int f = 0; f < fields; f++) q.append("field").append(f).append(';');
                    q.toString();
                }
                sbuf += ms(t);
            }
            System.out.printf("tickets=%d fields=%d | String(+)=%.2f  StringBuilder=%.2f  StringBuffer=%.2f%n",
                    tickets, fields, s / RUNS, sb / RUNS, sbuf / RUNS);
            // estimated intermediate String objects created by '+' version
            System.out.println("   estimated temporary Strings with '+': " + (long) tickets * fields * 2);
        }
        memoryDemo();
    }

    static double ms(long start) { return (System.nanoTime() - start) / 1e6; }

    static void warmUp() {
        for (int i = 0; i < 2000; i++) {
            StringBuilder q = new StringBuilder();
            for (int f = 0; f < 20; f++) q.append("x").append(f);
            q.toString();
        }
    }

    static void memoryDemo() {
        Runtime rt = Runtime.getRuntime();
        System.gc();
        long before = rt.totalMemory() - rt.freeMemory();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100000; i++) sb.append("field").append(i).append(';');
        long after = rt.totalMemory() - rt.freeMemory();
        System.out.println("Memory used by one big StringBuilder (approx): " + (after - before) / 1024 + " KB");
    }
}
