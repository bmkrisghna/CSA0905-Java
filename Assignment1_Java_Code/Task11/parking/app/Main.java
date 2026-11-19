package parking.app;

import java.time.LocalDate;
import parking.model.*;
import parking.service.ParkingFacility;

public class Main {
    static int pass = 0, fail = 0;

    static void check(String name, Object expected, Object actual) {
        boolean ok = expected.equals(actual);
        if (ok) pass++; else fail++;
        System.out.printf("%-34s expected=%-8s actual=%-8s %s%n", name, expected, actual, ok ? "PASS" : "FAIL");
    }

    public static void main(String[] args) {
        Pass monthly = new MonthlyPass(LocalDate.now().plusDays(30));

        // ---- Normal operation: day scenario (hand total = 30 + 90 + 210 = 330) ----
        ParkingFacility pf = new ParkingFacility(new int[]{20});
        pf.enter(VehicleFactory.create("twowheeler", "TN01A1"), 600);
        pf.enter(VehicleFactory.create("car", "TN02B2"), 600);
        pf.enter(VehicleFactory.create("ev", "TN03C3"), 600);
        double f1 = pf.exit("TN01A1", 730, null);
        double f2 = pf.exit("TN02B2", 730, null);
        double f3 = pf.exit("TN03C3", 730, null, 10);
        check("Two-wheeler 130 min", 30.0, f1);
        check("Car 130 min", 90.0, f2);
        check("EV 130 min + 10 kWh", 210.0, f3);
        check("Total collection of the day", 330.0, f1 + f2 + f3);

        // ---- Boundary cases ----
        pf.enter(new Car("TN10Z1"), 0);   check("Car 0 minutes", 0.0, pf.exit("TN10Z1", 0, null));
        pf.enter(new Car("TN10Z2"), 0);   check("Car exactly 60 min", 30.0, pf.exit("TN10Z2", 60, null));
        pf.enter(new Car("TN10Z3"), 0);   check("Car 61 min (2 hours)", 60.0, pf.exit("TN10Z3", 61, null));

        ParkingFacility tiny = new ParkingFacility(new int[]{1});
        check("Last free slot given", true, tiny.enter(new Car("TN20A1"), 0) != null);
        check("Facility full -> null", true, tiny.enter(new Car("TN20A2"), 0) == null);

        // ---- Pass holders ----
        pf.enter(new Car("TN30S1"), 0);
        check("Student with monthly pass", 0.0, pf.exit("TN30S1", 300, monthly));

        // ---- Invalid cases ----
        try { VehicleFactory.create("bus", "TN40B1"); check("Unknown type", "rejected", "accepted"); }
        catch (IllegalArgumentException e) { check("Unknown type", "rejected", "rejected"); }

        pf.enter(new Car("TN50D1"), 0);
        try { pf.enter(new Car("TN50D1"), 5); check("Duplicate reg number", "rejected", "accepted"); }
        catch (IllegalStateException e) { check("Duplicate reg number", "rejected", "rejected"); }

        check("Invalid slot release(5,0)", false, pf.release(5, 0));

        System.out.println("\nPASSED: " + pass + "   FAILED: " + fail);
    }
}
