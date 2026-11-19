import java.util.regex.Pattern;

/**
 * Alternative concept: escape quotes / validate with a regular expression instead of PreparedStatement.
 * No database needed: the program prints the SQL text that would be sent and measures the validation cost.
 */
public class RegexAlternative {
    static String escapeQuotes(String s) { return s.replace("'", "''"); }          // "escape the quote" approach
    static final Pattern SAFE = Pattern.compile("[A-Za-z0-9_]{1,30}");              // "whitelist" approach

    static String build(String id, String pwd) { return "SELECT * FROM users WHERE id='" + id + "' AND pwd='" + pwd + "'"; }

    public static void main(String[] args) {
        System.out.println("1) Quote escaping is NOT enough. In MySQL a backslash escapes the next character:");
        String attack = "\\' OR 1=1 -- ";                                           // the input is:  \' OR 1=1 --
        System.out.println("   input        : " + attack);
        System.out.println("   after escape : " + escapeQuotes(attack));
        System.out.println("   SQL sent     : " + build(escapeQuotes(attack), "x"));
        System.out.println("   MySQL reads  \\'' as: literal quote (\\') then a quote that CLOSES the string -> the OR 1=1 is executed as SQL.");

        System.out.println("\n2) A whitelist regex blocks this input, but:");
        System.out.println("   - passwords and names legitimately contain symbols (O'Neil, p@ss'word) and would be rejected;");
        System.out.println("   - every new field needs its own rule and every developer must remember to call it;");
        System.out.println("   - one forgotten call = injection. PreparedStatement protects every value automatically.");

        String[] inputs = {"admin", "admin' -- ", "x' OR '1'='1", "O'Neil", "S3cret!"};
        for (String in : inputs) System.out.printf("   %-16s matches whitelist? %s%n", in, SAFE.matcher(in).matches());

        int n = 2_000_000; long sink = 0;
        for (int i = 0; i < 200_000; i++) sink += SAFE.matcher(inputs[i % inputs.length]).matches() ? 1 : 0;
        long t = System.nanoTime();
        for (int i = 0; i < n; i++) sink += SAFE.matcher(inputs[i % inputs.length]).matches() ? 1 : 0;
        System.out.printf("%n3) Cost: %d regex validations = %.1f ms (%.0f ns each, O(length of input)). A database round trip costs about 1,000,000 ns or more,%n   so the validation time does not matter - the weakness is correctness, not speed (sink %d).%n",
                n, (System.nanoTime() - t) / 1e6, (System.nanoTime() - t) / (double) n, sink);
    }
}
