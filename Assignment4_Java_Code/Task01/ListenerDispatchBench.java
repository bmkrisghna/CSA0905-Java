import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Alternative concept: one if-else listener (up to k string comparisons) vs one listener object per button (direct call). */
public class ListenerDispatchBench {
    static long sink = 0;

    public static void main(String[] args) {
        for (int k : new int[]{3, 30, 300}) {
            String[] commands = new String[k];
            ActionListener[] perButton = new ActionListener[k];
            for (int i = 0; i < k; i++) { commands[i] = "Button" + i; final int id = i; perButton[i] = e -> sink += id; }

            // the "big if-else" listener: compares the command with each name until a match is found
            ActionListener big = e -> {
                String c = e.getActionCommand();
                for (int i = 0; i < commands.length; i++) { if (commands[i].equals(c)) { sink += i; break; } }
            };
            ActionEvent[] events = new ActionEvent[k];
            for (int i = 0; i < k; i++) events[i] = new ActionEvent(commands, ActionEvent.ACTION_PERFORMED, commands[i]);

            int rounds = 2_000_000 / k;
            for (int w = 0; w < 2; w++) {                              // first round = warm-up
                long t = System.nanoTime();
                for (int r = 0; r < rounds; r++) for (int i = 0; i < k; i++) big.actionPerformed(events[k - 1 - i]);
                double bigMs = (System.nanoTime() - t) / 1e6;
                t = System.nanoTime();
                for (int r = 0; r < rounds; r++) for (int i = 0; i < k; i++) perButton[k - 1 - i].actionPerformed(events[k - 1 - i]);
                double eachMs = (System.nanoTime() - t) / 1e6;
                if (w == 1) System.out.printf("k=%-4d %d events: if-else chain = %.1f ms | one listener per button = %.1f ms%n", k, rounds * k, bigMs, eachMs);
            }
        }
        System.out.println("(sink=" + sink + ")  Space: the if-else design has 1 listener object, the other has k small objects = O(k).");
    }
}
