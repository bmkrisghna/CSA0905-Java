public class WrapperDemo {
    public static void main(String[] args) {
        Integer a = 100, b = 100;        // cached range -128..127
        Integer c = 1000, d = 1000;      // outside cache -> different objects
        System.out.println("100  == 100  : " + (a == b));
        System.out.println("1000 == 1000 : " + (c == d) + "   (compares references - risky)");
        System.out.println("1000 equals  : " + c.equals(d) + "  (correct way)");

        // validating setter idea
        try {
            setDuration(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }
    static void setDuration(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Duration cannot be negative");
    }
}
