import java.io.*;
import java.nio.file.*;

/** Q1 - ATM withdrawal with try / catch (multi-catch) / finally. Runs a table of test inputs. */
public class AtmWithdrawal {

    static class InsufficientFundsException extends Exception {          // checked
        InsufficientFundsException(String msg) { super(msg); }
    }

    static double balance = 5000;
    static int finallyCount = 0;
    static String caughtBy = "";

    /** Reads the card file (checked exception: FileNotFoundException / IOException). */
    static void readCard(String cardFile) throws IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(cardFile))) {
            r.readLine();
        }
    }

    static String withdraw(String cardFile, String accountText, String amountText) {
        caughtBy = "none (success)";
        try {
            readCard(cardFile);                                   // checked
            long account = Long.parseLong(accountText);           // unchecked NumberFormatException / NPE
            double amount = Double.parseDouble(amountText);       // unchecked NumberFormatException / NPE
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");   // unchecked
            if (amount > balance) throw new InsufficientFundsException("Balance is only " + balance); // checked
            balance -= amount;
            return "Dispensed " + amount + " from account " + account + ", new balance " + balance;
        } catch (FileNotFoundException e) {                       // narrow IOException first
            caughtBy = "catch FileNotFoundException";
            return "Card reader error: " + e.getMessage();
        } catch (IOException e) {                                 // broader IOException after
            caughtBy = "catch IOException";
            return "I/O error: " + e.getMessage();
        } catch (InsufficientFundsException e) {
            caughtBy = "catch InsufficientFundsException";
            return "Declined: " + e.getMessage();
        } catch (NumberFormatException | NullPointerException e) {   // multi-catch
            caughtBy = "catch NumberFormatException | NullPointerException";
            return "Invalid number: " + e.getMessage();
        } catch (IllegalArgumentException e) {                    // NumberFormatException is its subclass,
            caughtBy = "catch IllegalArgumentException";          // so it MUST come after the narrower one
            return "Invalid amount: " + e.getMessage();
        } finally {
            finallyCount++;
            System.out.println("   [finally executed] card ejected (count=" + finallyCount + ")");
        }
    }

    public static void main(String[] args) throws IOException {
        Path card = Files.createTempFile("card", ".txt");
        Files.writeString(card, "CARD-1234\n");
        String ok = card.toString();

        String[][] tests = {
            // cardFile, account, amount
            {ok,                "1001",  "1500"},      // 1 valid
            {ok,                "10x1",  "500"},       // 2 letters in account number
            {ok,                "1001",  "abc"},       // 3 letters in amount
            {ok,                "1001",  "-200"},      // 4 negative amount
            {ok,                "1001",  "999999"},    // 5 more than balance
            {"missing_card.txt","1001",  "100"},       // 6 card-reader file missing
            {ok,                null,    "100"},       // 7 null input
        };

        System.out.printf("Starting balance = %.1f%n%n", balance);
        int no = 1;
        for (String[] t : tests) {
            System.out.println("Test " + no++ + ": account=" + t[1] + " amount=" + t[2] + " card=" + (t[0].equals(ok) ? "OK" : t[0]));
            String result = withdraw(t[0], t[1], t[2]);
            System.out.println("   Result  : " + result);
            System.out.println("   Path    : " + caughtBy);
        }
        System.out.println("\nfinally ran " + finallyCount + " times for " + tests.length + " tests");
    }
}
