/** Evidence: run() does NOT create a new thread, start() does. */
public class RunVsStart {
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> System.out.println("run()   executed by thread: " + Thread.currentThread().getName()), "worker-1");
        t1.run();                                    // normal method call -> runs in main
        Thread t2 = new Thread(() -> System.out.println("start() executed by thread: " + Thread.currentThread().getName()), "worker-2");
        t2.start();                                  // new thread
        t2.join();
        System.out.println("Calling start() twice on the same thread:");
        try { t2.start(); } catch (IllegalThreadStateException e) { System.out.println("   IllegalThreadStateException (thread already terminated)"); }
    }
}
