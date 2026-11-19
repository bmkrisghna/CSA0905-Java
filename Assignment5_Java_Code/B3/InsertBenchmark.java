import java.sql.*;
import java.util.*;

/**
 * B3.2 / B3.3 - Statement vs PreparedStatement vs batch, auto-commit vs transaction, and a forced failure half-way.
 *   java -cp ".:lib/*" InsertBenchmark 1000 10000 100000
 *   java -cp ".:lib/*" InsertBenchmark 1000 10000 --fast          (--fast: Statement/PreparedStatement only up to 10,000 rows)
 * WARNING: it deletes everything in bus_pass first. Use the TEST database only.
 * Tip for the report: repeat once with  &rewriteBatchedStatements=true  added to the url in db.properties.
 */
public class InsertBenchmark {
    static final String INSERT = "INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES (?,?,?,?,?)";

    static List<String[]> generate(int n) {
        List<String[]> l = new ArrayList<>(n);
        for (int i = 0; i < n; i++) l.add(new String[]{"P" + (100000 + i), String.format("%09d", i), "Student" + i, String.valueOf(1 + i % 40), String.valueOf(3000 + i % 2000)});
        return l;
    }
    static void clear(Connection con) throws SQLException { try (Statement st = con.createStatement()) { st.executeUpdate("DELETE FROM bus_pass"); } }
    static int count(Connection con) throws SQLException { try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM bus_pass")) { rs.next(); return rs.getInt(1); } }

    static double statementInsert(Connection con, List<String[]> rows) throws SQLException {          // (a) Statement, auto-commit ON
        con.setAutoCommit(true); long t = System.nanoTime();
        try (Statement st = con.createStatement()) {
            for (String[] p : rows) st.executeUpdate("INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES ('" + p[0] + "','" + p[1] + "','" + p[2] + "'," + p[3] + "," + p[4] + ")");
        }
        return (System.nanoTime() - t) / 1e6;
    }
    static double preparedInsert(Connection con, List<String[]> rows) throws SQLException {           // (b) PreparedStatement, auto-commit ON
        con.setAutoCommit(true); long t = System.nanoTime();
        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            for (String[] p : rows) { bind(ps, p); ps.executeUpdate(); }
        }
        return (System.nanoTime() - t) / 1e6;
    }
    static double batchAutoCommit(Connection con, List<String[]> rows) throws SQLException {          // (c) batch, auto-commit ON
        con.setAutoCommit(true); long t = System.nanoTime();
        try (PreparedStatement ps = con.prepareStatement(INSERT)) {
            int n = 0;
            for (String[] p : rows) { bind(ps, p); ps.addBatch(); if (++n % 1000 == 0) ps.executeBatch(); }
            ps.executeBatch();
        }
        return (System.nanoTime() - t) / 1e6;
    }
    static double batchTransaction(Connection con, List<String[]> rows) throws SQLException {         // (c) batch, ONE transaction (PassLoader)
        long t = System.nanoTime();
        PassLoader.loadAll(con, rows);
        return (System.nanoTime() - t) / 1e6;
    }
    static void bind(PreparedStatement ps, String[] p) throws SQLException {
        ps.setString(1, p[0]); ps.setString(2, p[1]); ps.setString(3, p[2]); ps.setInt(4, Integer.parseInt(p[3])); ps.setDouble(5, Double.parseDouble(p[4]));
    }

    public static void main(String[] args) throws Exception {
        boolean fast = false; List<Integer> sizes = new ArrayList<>();
        for (String a : args) { if (a.equals("--fast")) fast = true; else sizes.add(Integer.parseInt(a)); }
        if (sizes.isEmpty()) { sizes.add(1000); sizes.add(10000); sizes.add(100000); }

        try (Connection con = Db.connect()) {
            Db.createBusPassTable(con);
            System.out.printf("%-9s %-18s %-18s %-22s %-22s%n", "rows", "Statement (ms)", "Prepared (ms)", "Batch auto-commit (ms)", "Batch transaction (ms)");
            for (int n : sizes) {
                List<String[]> rows = generate(n);
                String a = "skipped", b = "skipped";
                if (!(fast && n > 10000)) { clear(con); a = String.format("%.0f", statementInsert(con, rows)); clear(con); b = String.format("%.0f", preparedInsert(con, rows)); }
                clear(con); String c = String.format("%.0f", batchAutoCommit(con, rows));
                clear(con); String d = String.format("%.0f", batchTransaction(con, rows));
                System.out.printf("%-9d %-18s %-18s %-22s %-22s%n", n, a, b, c, d);
            }

            // ---- B3.3: force a failure half-way (invalid amount in the middle of 2,000 rows) ----
            System.out.println("\nForced failure at row 1,000 of 2,000 (amount = \"abc\"):");
            List<String[]> bad = generate(2000); bad.get(1000)[4] = "abc";

            clear(con);
            try { batchAutoCommit(con, bad); } catch (RuntimeException | SQLException e) { System.out.println("   auto-commit batch failed: " + e); }
            System.out.println("   rows left in table after the failure, AUTO-COMMIT   : " + count(con) + "   (partial data stays!)");

            clear(con);
            try { batchTransaction(con, bad); } catch (RuntimeException | SQLException e) { System.out.println("   transaction failed: " + e); }
            System.out.println("   rows left in table after the failure, TRANSACTION   : " + count(con) + "   (everything rolled back)");
            clear(con);
        }
    }
}
