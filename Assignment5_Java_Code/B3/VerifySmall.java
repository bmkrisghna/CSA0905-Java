import java.sql.*;
import java.util.*;

/** B3.1 - insert 10 records with Statement, PreparedStatement and batch; verify with SELECT + ResultSetMetaData. */
public class VerifySmall {
    static List<String[]> data(String prefix) {
        List<String[]> l = new ArrayList<>();
        for (int i = 1; i <= 10; i++) l.add(new String[]{prefix + i, String.format("19231%04d", i), "Student" + i, String.valueOf(1 + i % 5), String.valueOf(3000 + i * 10.5)});
        return l;
    }

    public static void main(String[] args) throws Exception {
        try (Connection con = Db.connect()) {
            Db.createBusPassTable(con);
            try (Statement st = con.createStatement()) { st.executeUpdate("DELETE FROM bus_pass"); }

            try (Statement st = con.createStatement()) {                                                  // (a) Statement
                for (String[] p : data("S")) st.executeUpdate("INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES ('"
                        + p[0] + "','" + p[1] + "','" + p[2] + "'," + p[3] + "," + p[4] + ")");
            }
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO bus_pass(pass_id, reg_no, name, route_no, amount) VALUES (?,?,?,?,?)")) {   // (b)
                for (String[] p : data("P")) { ps.setString(1, p[0]); ps.setString(2, p[1]); ps.setString(3, p[2]); ps.setInt(4, Integer.parseInt(p[3])); ps.setDouble(5, Double.parseDouble(p[4])); ps.executeUpdate(); }
            }
            PassLoader.loadAll(con, data("B"));                                                          // (c) batch + transaction

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM bus_pass ORDER BY pass_id")) {
                ResultSetMetaData md = rs.getMetaData();
                System.out.println("Columns (ResultSetMetaData):");
                for (int i = 1; i <= md.getColumnCount(); i++)
                    System.out.printf("   %d  %-10s %-10s size=%d%n", i, md.getColumnName(i), md.getColumnTypeName(i), md.getPrecision(i));
                int rows = 0;
                while (rs.next()) { rows++; System.out.printf("   %-5s %-10s %-10s route=%d amount=%.2f%n", rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getDouble(5)); }
                System.out.println("Rows in table: " + rows + " (expected 30 = 10 per approach)");
            }
        }
    }
}
