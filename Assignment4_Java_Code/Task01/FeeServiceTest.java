/** Console test of FeeService - no window needed. This is why the redesign is easy to test. */
public class FeeServiceTest {
    static int pass = 0, fail = 0;

    static void expectOk(String name, FeeService s, String reg, String amt) {
        try { Receipt r = s.pay(reg, amt); report(name, true, "OK -> " + r); }
        catch (FeeException e) { report(name, false, "unexpected: " + e.getMessage()); }
    }
    static void expectError(String name, FeeService s, String reg, String amt, String part) {
        try { s.pay(reg, amt); report(name, false, "accepted but should be rejected"); }
        catch (FeeException e) { report(name, e.getMessage().contains(part), "rejected: " + e.getMessage()); }
    }
    static void report(String name, boolean ok, String detail) {
        if (ok) pass++; else fail++;
        System.out.printf("%-28s %s  %s%n", name, ok ? "PASS" : "FAIL", detail);
    }

    public static void main(String[] args) {
        FeeService s = new FeeService(new InMemoryPaymentStore(0));
        expectOk("valid payment", s, "192311001", "4500");
        expectError("empty register number", s, "", "100", "9 digits");
        expectError("short register number", s, "1923", "100", "9 digits");
        expectError("letters in amount", s, "192311001", "abc", "must be a number");
        expectError("empty amount", s, "192311001", "", "must be a number");
        expectError("negative amount", s, "192311001", "-50", "greater than zero");
        expectError("huge amount", s, "192311001", "9999999", "more than");
        expectError("SQL injection text", s, "1' OR '1'='1", "100", "9 digits");
        System.out.println("\nPASSED " + pass + "  FAILED " + fail);
    }
}
