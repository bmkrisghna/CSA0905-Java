/** Uncaught exception: run it and explain every line of the stack trace. */
public class StackTraceDemo {
    static int divide(int a, int b) { return a / b; }           // line 3: exception is created here
    static int calculate(int x)     { return divide(x, 0); }    // line 4: called from here
    static int atmMenu()            { return calculate(100); }  // line 5

    public static void main(String[] args) {
        System.out.println("Before the failure");
        System.out.println(atmMenu());                          // nobody catches ArithmeticException
        System.out.println("This line is never reached");
    }
}
