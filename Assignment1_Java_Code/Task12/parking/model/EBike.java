package parking.model;

/** E-bike: flat fee, no hourly rate. */
public class EBike extends Vehicle {
    public EBike(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 0; }
    @Override public double calculateFee(int minutes) {
        billableHours(minutes);          // only to validate the minutes
        return 20;                       // flat fee
    }
}
