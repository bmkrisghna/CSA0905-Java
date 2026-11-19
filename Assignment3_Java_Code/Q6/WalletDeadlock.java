import java.lang.management.*;

/** Part A: two opposite transfers that reliably deadlock. The program detects the deadlock itself. */
public class WalletDeadlock {
    static class Wallet {
        final int id; int balance;
        Wallet(int id, int balance) { this.id = id; this.balance = balance; }
    }

    static void transfer(Wallet from, Wallet to, int amount) throws InterruptedException {
        synchronized (from) {                                      // lock 1
            System.out.println(Thread.currentThread().getName() + " locked wallet " + from.id);
            Thread.sleep(200);                                     // gives the other thread time to take its first lock
            synchronized (to) {                                    // lock 2
                from.balance -= amount; to.balance += amount;
                System.out.println(Thread.currentThread().getName() + " transferred " + amount);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Wallet a = new Wallet(1, 1000), b = new Wallet(2, 1000);
        Thread t1 = new Thread(() -> { try { transfer(a, b, 100); } catch (InterruptedException e) { } }, "Transfer-A-to-B");
        Thread t2 = new Thread(() -> { try { transfer(b, a, 50); }  catch (InterruptedException e) { } }, "Transfer-B-to-A");
        t1.setDaemon(true); t2.setDaemon(true);
        t1.start(); t2.start();
        Thread.sleep(1000);

        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] ids = mx.findDeadlockedThreads();
        if (ids == null) {
            System.out.println("No deadlock this time.");
        } else {
            System.out.println("\nDEADLOCK DETECTED between:");
            for (ThreadInfo ti : mx.getThreadInfo(ids)) {
                System.out.println("   " + ti.getThreadName() + " is waiting for a lock held by " + ti.getLockOwnerName());
            }
            System.out.println("Four conditions: mutual exclusion (synchronized), hold and wait (holds 1st lock, wants 2nd),");
            System.out.println("                 no preemption (locks cannot be taken away), circular wait (A waits for B, B waits for A).");
        }
        System.out.println("Balances unchanged: A=" + a.balance + " B=" + b.balance);
    }
}
