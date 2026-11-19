/** All business rules live here. No AWT/Swing code, so it can be tested without a window. */
public class FeeService {
    public static final double MAX_AMOUNT = 500_000;
    private final PaymentStore store;
    private Receipt lastReceipt;

    public FeeService(PaymentStore store) { this.store = store; }

    /** Validates the input, stores the payment and returns the receipt. */
    public Receipt pay(String regNo, String amountText) throws FeeException {
        String reg = regNo == null ? "" : regNo.trim();
        if (!reg.matches("\\d{9}")) throw new FeeException("Register number must be exactly 9 digits");
        double amount;
        try {
            amount = Double.parseDouble(amountText == null ? "" : amountText.trim());
        } catch (NumberFormatException e) {
            throw new FeeException("Amount must be a number", e);
        }
        if (amount <= 0) throw new FeeException("Amount must be greater than zero");
        if (amount > MAX_AMOUNT) throw new FeeException("Amount cannot be more than " + (long) MAX_AMOUNT);

        int number = store.save(reg, amount);
        lastReceipt = new Receipt(number, reg, amount);
        return lastReceipt;
    }

    public Receipt lastReceipt() throws FeeException {
        if (lastReceipt == null) throw new FeeException("No payment has been made yet");
        return lastReceipt;
    }
}
