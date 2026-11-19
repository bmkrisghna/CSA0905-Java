import java.sql.*;
import java.util.*;

/**
 * Alternative concept: (1) ONE multi-row INSERT per 1,000 rows, (2) MySQL LOAD DATA.
 * LOAD DATA LOCAL needs  allowLoadLocalInfile=true  in the JDBC url and  SET GLOBAL local_infile=1  on the server.
 */
public class AlternativeInserts {
    public static void main(String[] args) throws Exception {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 10000;
        List<String[]> rows = InsertBenchmark.generate(n);
        try (Connection con = Db.connect()) {
            Db.createBusPassTable(con);

            InsertBenchmark.clear(con);
            long t = System.nanoTime();
            int b = 1000;                                                          // rows per statement
            con.setAutoCommit(false);
            for (int from = 0; from < n; from += b) {
                int len = Math.min(b, n - from);
                StringBuilder sql = new StringBuilder("INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES ");
                for (int i = 0; i < len; i++) sql.append(i == 0 ? "" : ",").append("(?,?,?,?,?)");
                try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                    int k = 1;
                    for (int i = from; i < from + len; i++) { String[] p = rows.get(i); ps.setString(k++, p[0]); ps.setString(k++, p[1]); ps.setString(k++, p[2]); ps.setInt(k++, Integer.parseInt(p[3])); ps.setDouble(k++, Double.parseDouble(p[4])); }
                    ps.executeUpdate();
                }
            }
            con.commit(); con.setAutoCommit(true);
            System.out.printf("multi-row INSERT (%d rows per statement): %d rows in %.0f ms%n", b, n, (System.nanoTime() - t) / 1e6);

            InsertBenchmark.clear(con);
            java.nio.file.Path csv = java.nio.file.Files.createTempFile("bus_pass", ".csv");
            try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(csv)) { for (String[] p : rows) { w.write(String.join(",", p)); w.newLine(); } }
            t = System.nanoTime();
            try (Statement st = con.createStatement()) {
                st.execute("LOAD DATA LOCAL INFILE '" + csv.toString().replace("\\", "/") + "' INTO TABLE bus_pass FIELDS TERMINATED BY ',' LINES TERMINATED BY '\\n' (pass_id, reg_no, name, route_no, amount)");
                System.out.printf("LOAD DATA: %d rows in %.0f ms%n", InsertBenchmark.count(con), (System.nanoTime() - t) / 1e6);
            } catch (SQLException e) {
                System.out.println("LOAD DATA not available: " + e.getMessage() + "\n(enable local_infile on the server and allowLoadLocalInfile=true in the url)");
            }
            java.nio.file.Files.deleteIfExists(csv);
            InsertBenchmark.clear(con);
        }
    }
}
