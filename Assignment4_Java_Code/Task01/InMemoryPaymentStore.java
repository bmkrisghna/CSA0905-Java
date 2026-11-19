import java.util.ArrayList;
import java.util.List;

/** Demo store: keeps payments in a list and simulates a slow bank confirmation. */
public class InMemoryPaymentStore implements PaymentStore {
    private final List<Receipt> payments = new ArrayList<>();
    private final long delayMillis;

    public InMemoryPaymentStore(long delayMillis) { this.delayMillis = delayMillis; }

    @Override public synchronized int save(String regNo, double amount) throws FeeException {
        try { Thread.sleep(delayMillis); }                      // "wait for bank confirmation" (runs OFF the event thread)
        catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new FeeException("Payment interrupted", e); }
        int number = payments.size() + 1;
        payments.add(new Receipt(number, regNo, amount));
        return number;
    }
    public synchronized int count() { return payments.size(); }
}
