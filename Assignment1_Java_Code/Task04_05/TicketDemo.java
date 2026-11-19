public class TicketDemo {
    public static void main(String[] args) {
        ParkingTicket t1 = new ParkingTicket("TN01A1");          // chained constructor
        ParkingTicket t2 = new ParkingTicket("TN02B2", 540);
        ParkingTicket dup = new ParkingTicket(t1);               // copy constructor (duplicate ticket)
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(dup + "  <- same ticket number as t1");
        System.out.println("Tickets issued so far: " + ParkingTicket.issuedCount());
        System.out.println("t1 == dup ? " + (t1 == dup) + "   (different objects in the heap)");
    }
}

class ParkingTicket {
    private static int lastTicketNo;          // static: shared by ALL tickets
    static { lastTicketNo = 0; }              // static block: runs once when class loads

    private final int ticketNo;
    private final String regNo;
    private final int entryMinute;

    ParkingTicket(String regNo) {             // overloaded + chained
        this(regNo, 0);
    }
    ParkingTicket(String regNo, int entryMinute) {
        this.ticketNo = ++lastTicketNo;       // unique, never reused
        this.regNo = regNo;
        this.entryMinute = entryMinute;
    }
    ParkingTicket(ParkingTicket other) {      // copy constructor, keeps the same number
        this.ticketNo = other.ticketNo;
        this.regNo = other.regNo;
        this.entryMinute = other.entryMinute;
    }
    static int issuedCount() { return lastTicketNo; }

    @Override public String toString() {
        return "Ticket#" + ticketNo + " [" + regNo + ", entry=" + entryMinute + " min]";
    }
}
