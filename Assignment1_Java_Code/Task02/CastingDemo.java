public class CastingDemo {
    public static void main(String[] args) {
        int minutes = 90;
        double wrong = minutes / 60 * 30;                 // integer division: 90/60 = 1
        double right = Math.ceil(minutes / 60.0) * 30;    // 1.5 -> 2 hours
        int rightInt = (minutes + 59) / 60 * 30;          // integer-only ceiling
        System.out.println("Wrong fee  : " + wrong);
        System.out.println("Right fee  : " + right);
        System.out.println("Right (int): " + rightInt);
    }
}
