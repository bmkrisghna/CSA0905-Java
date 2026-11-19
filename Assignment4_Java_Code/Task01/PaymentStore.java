/** What FeeService needs from storage. Swap the implementation without touching FeeService or the window. */
public interface PaymentStore {
    int save(String regNo, double amount) throws FeeException;   // returns the new receipt number
}
