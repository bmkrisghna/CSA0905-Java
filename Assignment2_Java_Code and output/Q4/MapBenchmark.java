import java.util.*;

public class MapBenchmark {
    public static void main(String[] args) {
        for (int n : new int[]{1000, 10000, 100000}) {
            Map<Integer, String> hash = new HashMap<>();
            Map<Integer, String> tree = new TreeMap<>();
            Map<Integer, String> linked = new LinkedHashMap<>();
            Random rnd = new Random(1);
            List<Integer> codes = new ArrayList<>();
            for (int i = 0; i < n; i++) codes.add(i * 7 + 1000);
            Collections.shuffle(codes, rnd);                      // added in random order
            for (int c : codes) { hash.put(c, "M" + c); tree.put(c, "M" + c); linked.put(c, "M" + c); }

            int[] keys = new int[100000];
            for (int i = 0; i < keys.length; i++) keys[i] = codes.get(rnd.nextInt(n));

            System.out.printf("n=%d%n", n);
            lookup("HashMap", hash, keys);
            lookup("TreeMap", tree, keys);
            lookup("LinkedHashMap", linked, keys);

            // reports
            long t = System.nanoTime();
            int first = tree.keySet().iterator().next();                       // code-order report (TreeMap)
            double tr = (System.nanoTime() - t) / 1e6;
            t = System.nanoTime();
            List<Integer> sorted = new ArrayList<>(hash.keySet());             // wrong map -> must sort
            Collections.sort(sorted);
            double sortHash = (System.nanoTime() - t) / 1e6;
            System.out.printf("   code-order report: TreeMap iterate=%.2f ms | HashMap + sort=%.2f ms%n", tr, sortHash);
            System.out.println("   LinkedHashMap first added code = " + linked.keySet().iterator().next()
                    + " (matches codes.get(0) = " + codes.get(0) + ")");
        }
    }

    static void lookup(String name, Map<Integer, String> m, int[] keys) {
        for (int w = 0; w < 2; w++) {                              // warm-up round + measured round
            long t = System.nanoTime();
            long len = 0;
            for (int k : keys) len += m.get(k).length();
            double ms = (System.nanoTime() - t) / 1e6;
            if (w == 1) System.out.printf("   %-14s 100,000 lookups = %.2f ms%n", name, ms);
        }
    }
}
