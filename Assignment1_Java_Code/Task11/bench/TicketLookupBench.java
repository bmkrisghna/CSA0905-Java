import java.util.*;

public class TicketLookupBench {
    public static void main(String[] args) {
        for (int n : new int[]{1000, 10000, 100000}) {
            String[] array = new String[n];
            List<String> list = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < n; i++) {
                String reg = "TN" + i;
                array[i] = reg; list.add(reg); map.put(reg, reg);
            }
            String target = "TN" + (n - 1);          // worst case for linear search
            int reps = 1000;

            long t = System.nanoTime();
            for (int r = 0; r < reps; r++) { for (String s : array) if (s.equals(target)) break; }
            double arr = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            for (int r = 0; r < reps; r++) list.indexOf(target);
            double lst = (System.nanoTime() - t) / 1e6;

            t = System.nanoTime();
            for (int r = 0; r < reps; r++) map.get(target);
            double mp = (System.nanoTime() - t) / 1e6;

            System.out.printf("n=%d  array=%.2f ms  ArrayList=%.2f ms  HashMap=%.3f ms  (1000 lookups)%n", n, arr, lst, mp);
        }
    }
}
