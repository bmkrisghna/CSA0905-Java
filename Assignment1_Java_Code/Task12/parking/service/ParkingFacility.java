package parking.service;

import java.util.HashMap;
import java.util.Map;
import parking.model.Chargeable;
import parking.model.Pass;
import parking.model.ParkingTicket;
import parking.model.Vehicle;

public class ParkingFacility {
    private final String[][] slots;                                   // jagged array
    private final Map<String, ParkingTicket> active = new HashMap<>(); // regNo -> ticket

    public ParkingFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            if (slotsPerFloor[f] <= 0) throw new IllegalArgumentException("Slots must be > 0");
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    /** Parks the vehicle. Returns the ticket, or null if the facility is full. */
    public ParkingTicket enter(Vehicle v, int entryMinute) {
        if (active.containsKey(v.getRegNo()))
            throw new IllegalStateException("Vehicle already parked: " + v.getRegNo());
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = v.getRegNo();
                    ParkingTicket t = new ParkingTicket(v, f, s, entryMinute);
                    active.put(v.getRegNo(), t);
                    return t;
                }
            }
        }
        return null;
    }

    public double exit(String regNo, int exitMinute, Pass pass) {
        return exit(regNo, exitMinute, pass, 0);
    }

    /** Releases the slot and returns the fee (parking fee + charging fee). */
    public double exit(String regNo, int exitMinute, Pass pass, int kWh) {
        String key = regNo.trim().toUpperCase();
        ParkingTicket t = active.get(key);
        if (t == null) throw new IllegalArgumentException("No active ticket for " + key);
        int minutes = exitMinute - t.getEntryMinute();
        if (minutes < 0) throw new IllegalArgumentException("Exit time is before entry time");

        Vehicle v = t.getVehicle();
        double parkingFee = (pass != null && pass.isValid())
                ? 0
                : v.calculateFee(minutes, (t.getEntryMinute() / 60) % 24);
        double charging = (v instanceof Chargeable c) ? c.chargingFee(kWh) : 0;

        release(t.getFloor(), t.getSlot());
        active.remove(key);
        return parkingFee + charging;
    }

    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        return true;
    }

    public int occupied() { return active.size(); }
}
