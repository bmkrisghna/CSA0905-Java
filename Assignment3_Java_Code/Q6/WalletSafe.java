import java.util.*;

/** Part A fix: always lock the wallet with the SMALLER id first (lock ordering) -> circular wait is impossible. */
public class WalletSafe {
    static class Wallet {
        final int id; int balance;
        Wallet(int id, int balance) { this.id = id; this.balance = balance; }
    }

    static void transfer(Wallet from, Wallet to, int amount) {
        Wallet first = from.id < to.id ? from : to;
        Wallet second = from.id < to.id ? to : from;
        synchronized (first) {
            synchronized (second) {
                if (from.balance >= amount) { from.balance -= amount; to.balance += amount; }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Wallet[] w = new Wallet[5];
        for (int i = 0; i < 5; i++) w[i] = new Wallet(i, 1000);
        Thread[] t = new Thread[8];
        for (int k = 0; k < t.length; k++) {
            t[k] = new Thread(() -> {
                Random r = new Random();
                for (int i = 0; i < 20_000; i++) {
                    int a = r.nextInt(5), b = r.nextInt(5);
                    if (a != b) transfer(w[a], w[b], 1 + r.nextInt(50));
                }
            });
            t[k].setDaemon(true);
            t[k].start();
        }
        for (Thread x : t) x.join(10_000);
        boolean finished = true;
        for (Thread x : t) if (x.isAlive()) finished = false;
        int total = 0; for (Wallet x : w) total += x.balance;
        System.out.println("All 160,000 transfers finished (no deadlock): " + finished);
        System.out.println("Total money = " + total + " (must stay 5000)");
    }
}
