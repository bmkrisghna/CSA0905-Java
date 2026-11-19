package parking.model;

/** Delivery van: double the car rate (60) if parking STARTS between 9 AM and 5 PM, else car rate (30). */
public class DeliveryVan extends Vehicle {
    public DeliveryVan(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 60; }
    @Override public double calculateFee(int minutes, int startHour) {
        double rate = (startHour >= 9 && startHour < 17) ? 60 : 30;
        return billableHours(minutes) * rate;
    }
}
