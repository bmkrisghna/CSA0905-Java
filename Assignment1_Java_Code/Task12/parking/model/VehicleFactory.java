package parking.model;

/** Static factory: creates a vehicle from its type name. (Modified in Task 12: 2 new cases.) */
public class VehicleFactory {
    private VehicleFactory() { }

    public static Vehicle create(String type, String regNo) {
        switch (type.trim().toLowerCase()) {
            case "twowheeler": return new TwoWheeler(regNo);
            case "car":        return new Car(regNo);
            case "ev":         return new ElectricCar(regNo);
            case "ebike":      return new EBike(regNo);
            case "van":        return new DeliveryVan(regNo);
            default: throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}
