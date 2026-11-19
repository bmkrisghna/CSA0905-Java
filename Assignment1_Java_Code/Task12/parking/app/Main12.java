package parking.app;

import java.time.LocalDate;
import parking.model.*;
import parking.service.ParkingFacility;

public class Main12 {
    static int pass = 0, fail = 0;

    static void check(String name, Object expected, Object actual) {
        boolean ok = expected.equals(actual);
        if (ok) pass++; else fail++;
        System.out.printf("%-38s expected=%-8s actual=%-8s %s%n", name, expected, actual, ok ? "PASS" : "FAIL");
    }

    public static void main(String[] args) {
        // 5 floors with a different number of slots on each floor
        ParkingFacility pf = new ParkingFacility(new int[]{20, 40, 60, 80, 100});

        pf.enter(VehicleFactory.create("ebike", "TN60E1"), 600);
        check("E-bike 90 min (flat fee)", 20.0, pf.exit("TN60E1", 690, null));

        pf.enter(VehicleFactory.create("van", "TN61V1"), 600);       // starts 10:00 -> peak
        check("Van 130 min starting 10:00", 180.0, pf.exit("TN61V1", 730, null));

        pf.enter(VehicleFactory.create("van", "TN61V2"), 1200);      // starts 20:00 -> off-peak
        check("Van 130 min starting 20:00", 90.0, pf.exit("TN61V2", 1330, null));

        pf.enter(VehicleFactory.create("car", "TN62C1"), 600);
        check("Visitor car with one-day pass", 0.0, pf.exit("TN62C1", 900, new OneDayPass(LocalDate.now())));

        pf.enter(VehicleFactory.create("car", "TN62C2"), 600);
        check("Visitor car with OLD one-day pass", 30.0 * 3, pf.exit("TN62C2", 730, new OneDayPass(LocalDate.now().minusDays(1))));

        pf.enter(VehicleFactory.create("ev", "TN63E1"), 600);
        check("Old type EV still works", 210.0, pf.exit("TN63E1", 730, null, 10));

        System.out.println("\nPASSED: " + pass + "   FAILED: " + fail);
    }
}
