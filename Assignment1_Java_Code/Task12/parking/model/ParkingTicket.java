package parking.model;

public class ParkingTicket {
    private static int lastTicketNo = 0;          // shared counter, never reused

    private final int ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final int entryMinute;                // minutes since midnight

    public ParkingTicket(Vehicle vehicle, int floor, int slot, int entryMinute) {
        this.ticketNo = ++lastTicketNo;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryMinute = entryMinute;
    }

    public ParkingTicket(ParkingTicket t) {       // copy constructor (duplicate ticket)
        this.ticketNo = t.ticketNo;
        this.vehicle = t.vehicle;
        this.floor = t.floor;
        this.slot = t.slot;
        this.entryMinute = t.entryMinute;
    }

    public int getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public int getEntryMinute() { return entryMinute; }

    @Override public String toString() {
        return "Ticket#" + ticketNo + " " + vehicle + " floor=" + floor + " slot=" + slot;
    }
}
