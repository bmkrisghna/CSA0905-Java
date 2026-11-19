import java.util.Arrays;

/** Alternative concept: sorted array + Arrays.binarySearch(). */
public class BinarySearchAlternative {
    public static void main(String[] args) {
        int n = 100000;
        int[] codes = new int[n];
        for (int i = 0; i < n; i++) codes[i] = i * 7 + 1000;     // already sorted
        long t = System.nanoTime();
        long found = 0;
        for (int i = 0; i < 100000; i++) if (Arrays.binarySearch(codes, codes[(i * 31) % n]) >= 0) found++;
        System.out.printf("binarySearch: 100,000 lookups = %.2f ms (found %d)%n", (System.nanoTime() - t) / 1e6, found);
        // insertion would need shifting: O(n). Code-order report: O(n), already sorted.
    }
}
