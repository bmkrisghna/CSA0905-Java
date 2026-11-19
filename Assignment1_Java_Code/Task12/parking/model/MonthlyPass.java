package parking.model;

import java.time.LocalDate;

public class MonthlyPass implements Pass {
    private final LocalDate expiry;
    public MonthlyPass(LocalDate expiry) { this.expiry = expiry; }
    @Override public boolean isValid() { return !expiry.isBefore(LocalDate.now()); }
}
