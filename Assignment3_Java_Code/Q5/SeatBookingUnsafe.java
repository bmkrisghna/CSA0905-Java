/**
 * Same program but WITHOUT synchronized. Run it 10 times and record "Seats sold".
 *   java SeatBookingUnsafe          -> normal run (the race is rare, you may see 10 every time)
 *   java SeatBookingUnsafe slow     -> adds Thread.yield() between check and book, so the race shows up reliably
 * (BookingRace.java does the same with 10,000 requests and 20 repeated runs.)
 */
public class SeatBookingUnsafe {
    private final boolean[] booked;
    private int bookingsMade = 0;
    private static boolean widenWindow = false;

    SeatBookingUnsafe(int seats) { booked = new boolean[seats]; }

    void book(int seat) {                // no synchronized -> race condition
        if (!booked[seat]) {             // 1. check
            if (widenWindow) Thread.yield();
            booked[seat] = true;         // 2. book
            bookingsMade++;              // 3. count
        }
    }

    public static void main(String[] args) throws InterruptedException {
        widenWindow = args.length > 0;
        SeatBookingUnsafe screen = new SeatBookingUnsafe(10);
        Thread[] users = new Thread[100];
        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10;
            users[i] = new Thread(() -> screen.book(seat));
            users[i].start();
        }
        for (Thread t : users) t.join();
        System.out.println("Seats sold: " + screen.bookingsMade + "   (expected 10)");
    }
}
