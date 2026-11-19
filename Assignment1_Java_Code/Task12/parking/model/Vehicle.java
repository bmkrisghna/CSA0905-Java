package parking.model;

public abstract class Vehicle {
    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank())
            throw new IllegalArgumentException("Registration number is required");
        this.regNo = regNo.trim().toUpperCase();
    }

    public String getRegNo() { return regNo; }

    protected abstract double ratePerHour();

    /** Every started hour is charged in full (integer ceiling). */
    protected static int billableHours(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Minutes cannot be negative");
        return (minutes + 59) / 60;
    }

    // overloaded methods (compile-time polymorphism)
    public double calculateFee(int minutes) {
        return billableHours(minutes) * ratePerHour();
    }
    public double calculateFee(int minutes, int startHour) {
        return calculateFee(minutes);          // most vehicles ignore the time of day
    }

    @Override public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }
    @Override public int hashCode() { return regNo.hashCode(); }
    @Override public String toString() { return getClass().getSimpleName() + "[" + regNo + "]"; }
}
