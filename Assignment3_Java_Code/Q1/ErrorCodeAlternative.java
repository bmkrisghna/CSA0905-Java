/** Alternative concept: no exceptions, only input checks and error codes. Also measures the cost. */
public class ErrorCodeAlternative {
    static double balance = 5000;

    // returns: >=0 new balance, -1 bad account, -2 bad amount, -3 negative/zero, -4 insufficient funds
    static double withdrawCode(String account, String amountText) {
        if (account == null || !account.matches("\\d+")) return -1;
        if (amountText == null || !amountText.matches("-?\\d+(\\.\\d+)?")) return -2;
        double amount = Double.parseDouble(amountText);
        if (amount <= 0) return -3;
        if (amount > balance) return -4;
        return balance - amount;
    }

    static double withdrawException(String account, String amountText) {
        try {
            Long.parseLong(account);
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new IllegalArgumentException("not positive");
            if (amount > balance) throw new IllegalStateException("insufficient");
            return balance - amount;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        System.out.println("valid        : " + withdrawCode("1001", "500"));
        System.out.println("bad account  : " + withdrawCode("10x1", "500"));
        System.out.println("bad amount   : " + withdrawCode("1001", "abc"));
        System.out.println("negative     : " + withdrawCode("1001", "-5"));
        System.out.println("insufficient : " + withdrawCode("1001", "99999"));

        int n = 200_000;
        double sink = 0;
        for (int i = 0; i < 50_000; i++) { sink += withdrawCode("10x1", "abc"); sink += withdrawException("10x1", "abc"); }  // warm-up

        long t = System.nanoTime();
        for (int i = 0; i < n; i++) sink += withdrawCode("10x1", "abc");
        double codeMs = (System.nanoTime() - t) / 1e6;

        t = System.nanoTime();
        for (int i = 0; i < n; i++) sink += withdrawException("10x1", "abc");
        double excMs = (System.nanoTime() - t) / 1e6;

        System.out.printf("%d invalid inputs: error codes = %.2f ms, exceptions = %.2f ms (sink=%.0f)%n", n, codeMs, excMs, sink);
    }
}
