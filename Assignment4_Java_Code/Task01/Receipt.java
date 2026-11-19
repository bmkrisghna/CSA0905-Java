public record Receipt(int number, String regNo, double amount) {
    @Override public String toString() {
        return String.format("Receipt #%d | Reg No %s | Rs %.2f", number, regNo, amount);
    }
}
