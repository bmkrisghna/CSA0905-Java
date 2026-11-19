package parking.model;

import java.time.LocalDate;

/** One-day pass for visitors: valid only on the day it was bought for. */
public class OneDayPass implements Pass {
    private final LocalDate day;
    public OneDayPass(LocalDate day) { this.day = day; }
    @Override public boolean isValid() { return day.equals(LocalDate.now()); }
}
