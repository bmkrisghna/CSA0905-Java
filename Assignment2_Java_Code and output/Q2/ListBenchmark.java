import java.util.*;

public class ListBenchmark {
    public static void main(String[] args) {
        for (int n : new int[]{1000, 10000, 100000}) {
            run("ArrayList", new ArrayList<Integer>(), n);
            run("LinkedList", new LinkedList<Integer>(), n);
            runDeque(n);
            System.out.println();
        }
    }

    static double ms(long t) { return (System.nanoTime() - t) / 1e6; }

    static void run(String name, List<Integer> list, int n) {
        long t = System.nanoTime();
        for (int i = 0; i < n; i++) list.add(i);                       // add at end
        double addEnd = ms(t);

        t = System.nanoTime();
        for (int i = 0; i < 1000; i++) list.add(0, i);                 // insert at front
        double insFront = ms(t);

        t = System.nanoTime();
        for (int i = 0; i < 1000; i++) list.add(list.size() / 2, i);   // insert in middle
        double insMid = ms(t);

        t = System.nanoTime();
        long sum = 0;
        for (int i = 0; i < 1000; i++) sum += list.get(list.size() / 2);   // get k-th
        double get = ms(t);

        t = System.nanoTime();
        for (int x : list) sum += x;                                   // iterate
        double iter = ms(t);

        t = System.nanoTime();
        for (int i = 0; i < 1000; i++) list.remove(0);                 // remove front
        double remFront = ms(t);

        System.out.printf("n=%-7d %-10s addEnd=%.2f insFront=%.2f insMid=%.2f get-k=%.2f iterate=%.2f remFront=%.2f (ms)%n",
                n, name, addEnd, insFront, insMid, get, iter, remFront);
    }

    static void runDeque(int n) {
        Deque<Integer> d = new ArrayDeque<>();
        long t = System.nanoTime();
        for (int i = 0; i < n; i++) d.addLast(i);
        double addEnd = ms(t);
        t = System.nanoTime();
        for (int i = 0; i < 1000; i++) d.addFirst(i);
        double insFront = ms(t);
        t = System.nanoTime();
        long sum = 0;
        for (int x : d) sum += x;
        double iter = ms(t);
        t = System.nanoTime();
        for (int i = 0; i < 1000; i++) d.pollFirst();
        double remFront = ms(t);
        System.out.printf("n=%-7d %-10s addEnd=%.2f insFront=%.2f insMid=n/a  get-k=n/a  iterate=%.2f remFront=%.2f (ms)%n",
                n, "ArrayDeque", addEnd, insFront, iter, remFront);
    }
}
