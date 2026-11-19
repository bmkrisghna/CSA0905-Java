/** Shows the life-cycle states: NEW, RUNNABLE, TIMED_WAITING, BLOCKED, WAITING, TERMINATED. */
public class ThreadStates {
    public static void main(String[] args) throws Exception {
        Object lock = new Object();
        Thread sleeper = new Thread(() -> { try { Thread.sleep(500); } catch (InterruptedException e) { } });
        System.out.println("after new         : " + sleeper.getState());          // NEW
        sleeper.start();
        Thread.sleep(100);
        System.out.println("while sleeping    : " + sleeper.getState());          // TIMED_WAITING
        sleeper.join();
        System.out.println("after finishing   : " + sleeper.getState());          // TERMINATED

        Thread waiter = new Thread(() -> { synchronized (lock) { try { lock.wait(); } catch (InterruptedException e) { } } });
        waiter.start(); Thread.sleep(100);
        System.out.println("inside wait()     : " + waiter.getState());           // WAITING

        Thread blocked;
        synchronized (lock) {                                                      // main holds the lock
            blocked = new Thread(() -> { synchronized (lock) { } });
            blocked.start(); Thread.sleep(100);
            System.out.println("waiting for lock  : " + blocked.getState());      // BLOCKED
            lock.notifyAll();
        }
        blocked.join(); waiter.join();

        Thread busy = new Thread(() -> { long x = 0; for (long i = 0; i < 500_000_000L; i++) x += i; });
        busy.start(); Thread.sleep(50);
        System.out.println("running a loop    : " + busy.getState());             // RUNNABLE
        busy.join();
    }
}
