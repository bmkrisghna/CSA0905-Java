/**
 * A1.1(a) Delimited text record. Field order:
 *   passId , regNo , name , route , stop , validUntil , amount
 * The delimiter is a comma. A comma (or percent sign or newline) INSIDE a field is escaped as %2C, %25, %0A, so the
 * number of fields on a line is always exactly 7. A line that does not split into 7 fields is DAMAGED.
 */
public class TextRecordCodec {
    static String esc(String s) { return s.replace("%", "%25").replace(",", "%2C").replace("\n", "%0A"); }
    static String unesc(String s) { return s.replace("%0A", "\n").replace("%2C", ",").replace("%25", "%"); }

    static String encode(String id, String reg, String name, int route, String stop, String validUntil, double amount) {
        return String.join(",", esc(id), esc(reg), esc(name), String.valueOf(route), esc(stop), validUntil, String.valueOf(amount));
    }

    /** Returns the 7 decoded fields, or null if the line is damaged. */
    static String[] decode(String line) {
        String[] f = line.split(",", -1);
        if (f.length != 7) return null;
        try { Integer.parseInt(f[3]); Double.parseDouble(f[6]); } catch (NumberFormatException e) { return null; }
        for (int i = 0; i < f.length; i++) f[i] = unesc(f[i]);
        return f;
    }

    public static void main(String[] args) {
        String line = encode("P1001", "192311001", "Arun, K.", 12, "Main Gate", "2027-03-31", 4500);
        System.out.println("encoded : " + line);
        System.out.println("decoded : " + String.join(" | ", decode(line)));
        System.out.println("damaged : " + decode("P1002,DAMAGED LINE"));
        System.out.println("bad amount: " + decode("P1003,192311003,Divya,7,Stop,2027-03-31,abc"));
    }
}
