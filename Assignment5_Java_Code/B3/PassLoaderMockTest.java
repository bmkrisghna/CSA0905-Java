import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

/**
 * Tests PassLoader WITHOUT a database: java.lang.reflect.Proxy builds a fake Connection / PreparedStatement
 * that only records which methods were called. This checks the TODO 1-3 logic (batching, commit, rollback).
 */
public class PassLoaderMockTest {
    static List<String> calls;
    static int batchRows;

    static Connection fakeConnection(boolean failOnBatch) {
        calls = new ArrayList<>(); batchRows = 0;
        PreparedStatement ps = (PreparedStatement) Proxy.newProxyInstance(PassLoaderMockTest.class.getClassLoader(), new Class<?>[]{PreparedStatement.class},
            (proxy, m, a) -> {
                switch (m.getName()) {
                    case "addBatch": batchRows++; return null;
                    case "executeBatch": calls.add("executeBatch(" + batchRows + " rows pending)"); batchRows = 0;
                        if (failOnBatch) throw new SQLException("simulated database failure"); return new int[0];
                    case "close": return null;
                    default: return null;                              // setString, setInt, setDouble ...
                }
            });
        return (Connection) Proxy.newProxyInstance(PassLoaderMockTest.class.getClassLoader(), new Class<?>[]{Connection.class},
            (proxy, m, a) -> {
                switch (m.getName()) {
                    case "getAutoCommit": return true;
                    case "setAutoCommit": calls.add("setAutoCommit(" + a[0] + ")"); return null;
                    case "prepareStatement": return ps;
                    case "commit": calls.add("commit"); return null;
                    case "rollback": calls.add("rollback"); return null;
                    default: return null;
                }
            });
    }

    static List<String[]> rows(int n, int badAt) {
        List<String[]> l = new ArrayList<>();
        for (int i = 0; i < n; i++) l.add(new String[]{"P" + i, "192311001", "Name" + i, "5", i == badAt ? "abc" : "100.0"});
        return l;
    }

    public static void main(String[] args) throws Exception {
        int pass = 0, fail = 0;

        System.out.println("Test 1: 2,500 valid rows -> batches after 1,000 and 2,000, final batch, one commit, no rollback");
        Connection c = fakeConnection(false);
        int n = PassLoader.loadAll(c, rows(2500, -1));
        System.out.println("   calls: " + calls + "  returned " + n);
        boolean ok1 = n == 2500 && calls.equals(List.of("setAutoCommit(false)", "executeBatch(1000 rows pending)", "executeBatch(1000 rows pending)",
                "executeBatch(500 rows pending)", "commit", "setAutoCommit(true)"));
        System.out.println("   " + (ok1 ? "PASS" : "FAIL")); if (ok1) pass++; else fail++;

        System.out.println("Test 2: invalid amount at row 1,500 -> NumberFormatException, rollback, NO commit, exception re-thrown");
        c = fakeConnection(false);
        boolean thrown = false;
        try { PassLoader.loadAll(c, rows(2500, 1500)); } catch (NumberFormatException e) { thrown = true; }
        System.out.println("   calls: " + calls + "  exception re-thrown: " + thrown);
        boolean ok2 = thrown && calls.contains("rollback") && !calls.contains("commit") && calls.get(calls.size() - 1).equals("setAutoCommit(true)");
        System.out.println("   " + (ok2 ? "PASS" : "FAIL")); if (ok2) pass++; else fail++;

        System.out.println("Test 3: database error during executeBatch -> SQLException, rollback, NO commit");
        c = fakeConnection(true);
        thrown = false;
        try { PassLoader.loadAll(c, rows(1200, -1)); } catch (SQLException e) { thrown = true; }
        System.out.println("   calls: " + calls + "  exception re-thrown: " + thrown);
        boolean ok3 = thrown && calls.contains("rollback") && !calls.contains("commit");
        System.out.println("   " + (ok3 ? "PASS" : "FAIL")); if (ok3) pass++; else fail++;

        System.out.println("\nPASSED " + pass + "  FAILED " + fail);
    }
}
