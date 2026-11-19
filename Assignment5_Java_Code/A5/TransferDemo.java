import java.sql.*;

/**
 * A5.1 and its alternative - transfer Rs 500 from the hostel wallet to the canteen wallet.
 * The program fails ON PURPOSE between the two updates. Use the TEST database only.
 */
public class TransferDemo {
    static void show(Connection con, String label) throws SQLException {
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT owner, balance FROM wallet ORDER BY owner")) {
            StringBuilder sb = new StringBuilder(label + ": ");
            double total = 0;
            while (rs.next()) { sb.append(rs.getString(1)).append("=").append(rs.getDouble(2)).append("  "); total += rs.getDouble(2); }
            System.out.println(sb + "| total = " + total);
        }
    }
    static void reset(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS wallet (owner VARCHAR(30) PRIMARY KEY, balance DECIMAL(10,2) NOT NULL)");
            st.executeUpdate("DELETE FROM wallet");
            st.executeUpdate("INSERT INTO wallet VALUES ('canteen', 100), ('hostel', 1000)");
        }
    }

    /** one transaction: both updates succeed or none. failBetween = simulate a crash after the first update */
    static void transferTransaction(Connection con, double amount, boolean failBetween) throws SQLException {
        con.setAutoCommit(false);
        try (PreparedStatement debit = con.prepareStatement("UPDATE wallet SET balance = balance - ? WHERE owner = 'hostel' AND balance >= ?");
             PreparedStatement credit = con.prepareStatement("UPDATE wallet SET balance = balance + ? WHERE owner = 'canteen'")) {
            debit.setDouble(1, amount); debit.setDouble(2, amount);
            if (debit.executeUpdate() != 1) throw new SQLException("Insufficient balance");
            if (failBetween) throw new IllegalStateException("simulated crash between the two updates");
            credit.setDouble(1, amount); credit.executeUpdate();
            con.commit();
        } catch (SQLException | RuntimeException e) {
            con.rollback();                                                // undo the debit
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    /** two independent auto-commit statements: each one is permanent immediately */
    static void transferAutoCommit(Connection con, double amount, boolean failBetween) throws SQLException {
        con.setAutoCommit(true);
        try (Statement st = con.createStatement()) {
            st.executeUpdate("UPDATE wallet SET balance = balance - " + amount + " WHERE owner = 'hostel'");   // permanent now
            if (failBetween) throw new IllegalStateException("simulated crash between the two updates");
            st.executeUpdate("UPDATE wallet SET balance = balance + " + amount + " WHERE owner = 'canteen'");
        }
    }

    public static void main(String[] args) throws Exception {
        try (Connection con = Db.connect()) {
            reset(con); show(con, "start                    ");
            transferTransaction(con, 500, false); show(con, "transaction, no failure   ");

            reset(con);
            try { transferTransaction(con, 500, true); } catch (Exception e) { System.out.println("   -> " + e.getMessage()); }
            show(con, "transaction, crash        ");

            reset(con);
            try { transferAutoCommit(con, 500, true); } catch (Exception e) { System.out.println("   -> " + e.getMessage()); }
            show(con, "auto-commit, crash        ");
            System.out.println("With auto-commit the 500 left the hostel wallet and never reached the canteen: money was LOST.");

            // time cost: 1,000 transfers each way
            reset(con);
            long t = System.nanoTime();
            for (int i = 0; i < 1000; i++) transferTransaction(con, 0.01, false);
            double tx = (System.nanoTime() - t) / 1e6;
            t = System.nanoTime();
            for (int i = 0; i < 1000; i++) transferAutoCommit(con, 0.01, false);
            double ac = (System.nanoTime() - t) / 1e6;
            System.out.printf("1,000 transfers: one transaction each = %.0f ms | two auto-commit updates each = %.0f ms%n", tx, ac);
        }
    }
}
